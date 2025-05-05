package it.unibo.scafi.runtime

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.{ Export, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager

final class ScafiEngine[
    ID,
    Context <: AggregateContext { type DeviceId = ID },
    Network <: NetworkManager { type DeviceId = ID },
    Result,
](
    deviceId: ID,
    network: Network,
    factory: (ID, Network) => Context,
)(program: Context ?=> Result):
  private var lastExport: Export[ID] = Export(ValueTree.empty, Map.empty)
  private def round(): AggregateResult =
    val ctx: Context = factory(deviceId, network) // Here it is used the network (receive) for generate the context
    val result: Result = program(using ctx)
    val exportResult = ctx.exportFromOutboundMessages
    network.send(exportResult)
    AggregateResult(result, exportResult)

  /**
   * Executes a single round of the program.
   * @return
   *   the result of the single round.
   */
  def cycle(): Result =
    val cycleResult = round()
    lastExport = cycleResult.exportResult
    cycleResult.result

  /**
   * Retrieves the last [[Export]] result produced by the engine. If no round has been performed, it returns an empty
   * [[Export]].
   *
   * @return
   *   the last [[Export]].
   */
  def lastExportResult: Export[ID] = lastExport

  /**
   * Executes the program until the condition is met.
   * @param condition
   *   the condition to check.
   * @return
   *   the result of the last round.
   */
  def cycleWhile(condition: AggregateResult => Boolean): Result =
    var result: AggregateResult = round()
    while condition(result) do result = round()
    result.result

  /**
   * The result of the aggregate computation.
   * @param result
   *   the result of the computation.
   * @param exportResult
   *   the messages sent to the neighbors after the round.
   */
  final case class AggregateResult(
      result: Result,
      exportResult: Export[ID],
  )
end ScafiEngine
