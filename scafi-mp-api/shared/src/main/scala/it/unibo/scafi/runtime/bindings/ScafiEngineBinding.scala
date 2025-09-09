package it.unibo.scafi.runtime.bindings

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.PortableTypes
import it.unibo.scafi.runtime.{ PortableRuntime, ScafiEngine }
import it.unibo.scafi.runtime.network.sockets.ConnectionOrientedNetworkManager

/**
 * Provides a concrete implementation of the portable runtime API for the ScaFi engine.
 */
trait ScafiEngineBinding extends PortableRuntime:
  self: PortableTypes =>

  trait EngineBindings(using ExecutionContext) extends Api:
    self: Requirements =>

    override def engine[ID, Result](
        deviceId: ID,
        network: ConnectionOrientedNetworkManager[ID],
        program: Function1[AggregateLibrary, Result],
        onResult: Function1[Result, Outcome[Boolean]],
    ): Outcome[Unit] =
      def round: Future[Unit] =
        for
          engine = ScafiEngine(deviceId, network, exchangeContextFactory)(program(library))
          result <- Future(engine.cycle())
          continue <- onResult(result)
          _ <- if continue then round else Future.unit
        yield ()
      network
        .start()
        .flatMap(_ => round)
        .andThen(_ => network.close())
        .andThen:
          case Success(_) => ()
          case Failure(err) => Console.err.println(s"Error occurred: ${err.getMessage}")
    end engine
  end EngineBindings
end ScafiEngineBinding
