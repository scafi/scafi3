package it.unibo.scafi.runtime

import it.unibo.scafi.context.{ AggregateContext, ContextFactory }
import it.unibo.scafi.runtime.network.NetworkManager

final class ScafiEngine[
    ID,
    Context <: AggregateContext { type DeviceId = ID },
    Network <: NetworkManager { type DeviceId = ID },
    Result,
](
    deviceId: ID,
    network: Network,
    factory: ContextFactory[ID, Network, Context],
)(program: Context ?=> Result):
  private def round(): AggregateResult =
    val ctx: Context =
      factory.createContext(deviceId, network) // Here it is used the network (receive) for generate the context
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
