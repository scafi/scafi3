package it.unibo.scafi.runtime.bindings

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Try }

import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.runtime.{ PortableRuntime, ScafiEngine }
import it.unibo.scafi.runtime.network.sockets.ConnectionOrientedNetworkManager
import it.unibo.scafi.types.PortableTypes

/**
 * Provides a concrete implementation of the portable runtime API for the ScaFi engine.
 */
trait ScafiEngineBinding extends PortableRuntime:
  self: PortableTypes =>

  trait EngineBindings(using ExecutionContext) extends Api:
    self: Requirements & Adts =>

    /* WARNING: Inline is needed here for native platform to ensure function pointers are correctly handled at
     * call site. Removing it does not lead to compilation errors but to runtime segfaults! */
    inline override def engine[Result](
        network: ConnectionOrientedNetworkManager[DeviceId],
        program: Function1[AggregateLibrary, Result],
        onResult: Function1[Result, Outcome[Boolean]],
    ): Outcome[Unit] =
      network
        .start()
        .flatMap: _ =>
          val engine = ScafiEngine(network.deviceId, network, exchangeContextFactory)(safelyRun(program(library)))
          engine.asyncCycleWhile(onResult.apply).map(_ => ())
        .andThen(_ => Future(network.close()))
        .andThen(reportAnyFailure)

    private val reportAnyFailure: PartialFunction[Try[Unit], Unit] =
      case Failure(err) => Console.err.println(s"Error occurred: ${err.getMessage}")
  end EngineBindings
end ScafiEngineBinding
