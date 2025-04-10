package it.unibo.scafi.runtime

import it.unibo.scafi.runtime.context.{ AggregateContext, ContextFactory }
import it.unibo.scafi.runtime.network.Network as Net

final class ScafiEngine[DeviceId, Network <: Net[DeviceId], Context <: AggregateContext[DeviceId], Result](
    network: Network,
    factory: ContextFactory[DeviceId, Network, Context],
)(program: Context ?=> Result):
  private def round(): AggregateResult =
    val ctx: Context = factory.createContext(network) // Here it is used the network (receive) for generate the context
    val result: Result = program(using ctx)
    network.send(ctx.exportFromOutboundMessages)
    AggregateResult(result)

  def cycle(): Result = round().result

  def cycleWhile(condition: AggregateResult => Boolean): Result =
    var result: AggregateResult = round()
    while condition(result) do result = round()
    result.result

  final case class AggregateResult(result: Result)
end ScafiEngine
