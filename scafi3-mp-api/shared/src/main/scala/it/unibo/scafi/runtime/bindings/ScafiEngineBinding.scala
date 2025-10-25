package it.unibo.scafi.runtime.bindings

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Try }

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.runtime.{ PortableRuntime, ScafiEngine }
import it.unibo.scafi.runtime.network.sockets.{
  ConnectionConfiguration,
  ConnectionOrientedNetworkManager,
  SocketNetworkManager,
}
import it.unibo.scafi.types.PortableTypes

import io.github.iltotore.iron.refineUnsafe

/**
 * Provides a concrete implementation of the portable runtime API for the ScaFi engine.
 */
trait ScafiEngineBinding extends PortableRuntime:
  self: PortableTypes =>

  trait EngineBindings(using ExecutionContext) extends Api:
    self: Requirements & Adts =>

    private type Engine[Result] = ScafiEngine[
      DeviceId,
      ExchangeAggregateContext[DeviceId],
      ConnectionOrientedNetworkManager[DeviceId],
      Result,
    ]

    /* WARNING: Inline is needed here for native platform to ensure function pointers are correctly handled at
     * call site. Removing it does not lead to compilation errors but to runtime segfaults! */
    inline override def engine[ID, Result](
        deviceId: ID,
        port: Int,
        neighbors: Map[ID, Endpoint],
        program: Function1[AggregateLibrary, Result],
        onResult: Function1[Result, Outcome[Boolean]],
    ): Outcome[Unit] =
      deviceIdCodable.register(deviceId)
      val network = socketNetwork(deviceId, port, neighbors)
      network
        .start()
        .flatMap: _ =>
          ScafiEngine(deviceId: DeviceId, network, exchangeContextFactory)(safelyRun(program(library)))
            .cycleWhileAsync(onResult.apply)
        .andThen(_ => Future(network.close()))
        .andThen(reportAnyFailure)

    extension [Result](engine: Engine[Result])
      def cycleWhileAsync(onResult: Result => Future[Boolean]): Future[Unit] =
        for
          cycleResult <- Future(engine.cycle())
          outcome <- onResult(cycleResult)
          _ <- if outcome then engine.cycleWhileAsync(onResult) else Future.successful(())
        yield ()

    private def socketNetwork[ID](deviceId: ID, port: Int, neighbors: Map[ID, Endpoint]) =
      given ConnectionConfiguration = ConnectionConfiguration.basic
      val devicesNet = neighbors.view.map((id, e) => (id: DeviceId, toInetEndpoint(e))).toMap
      SocketNetworkManager.withFixedNeighbors(deviceId: DeviceId, port.refineUnsafe, devicesNet)

    private val reportAnyFailure: PartialFunction[Try[Unit], Unit] =
      case Failure(err) => Console.err.println(s"Error occurred: ${err.getMessage}")
  end EngineBindings
end ScafiEngineBinding
