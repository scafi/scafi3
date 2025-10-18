package it.unibo.scafi.runtime

import scala.annotation.tailrec
import scala.concurrent.{ ExecutionContext, Future }

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
    factory: (ID, Network, ValueTree) => Context,
)(program: Context ?=> Result):
  private var lastExport: Export[ID] = Export(ValueTree.empty, Map.empty)
  private var lastSelfMessages = ValueTree.empty

  private def round(): AggregateResult =
    val ctx = factory(deviceId, network, lastSelfMessages) // Here the network is used (receive) to generate the context
    val result = program(using ctx)
    val exportResult = ctx.exportFromOutboundMessages
    network.send(exportResult)
    AggregateResult(result, exportResult, ctx.selfMessagesForNextRound)

  /**
   * Executes a single round of the program.
   * @return
   *   the result of the single round.
   */
  def cycle(): Result = cycleWhile(_ => false)

  /**
   * Retrieves the last [[Export]] result produced by the engine. If no round has been performed, it returns an empty
   * [[Export]].
   *
   * @return
   *   the last [[Export]].
   */
  def lastExportResult: Export[ID] = lastExport

  def asyncCycleWhile(condition: Result => Future[Boolean])(using ExecutionContext): Future[Result] =
    for
      aggregateResult <- Future(round())
      _ = lastExport = aggregateResult.exportResult
      _ = lastSelfMessages = aggregateResult.selfMessages
      continue <- condition(aggregateResult.result)
      result <- if continue then asyncCycleWhile(condition) else Future.successful(aggregateResult.result)
    yield result

  /**
   * Executes the program until the condition is met.
   * @param condition
   *   the condition to check.
   * @return
   *   the result of the last round.
   */
  @tailrec
  def cycleWhile(condition: Result => Boolean): Result =
    val aggregateResult = round()
    lastExport = aggregateResult.exportResult
    lastSelfMessages = aggregateResult.selfMessages
    if condition(aggregateResult.result) then cycleWhile(condition) else aggregateResult.result

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
      selfMessages: ValueTree,
  )
end ScafiEngine
