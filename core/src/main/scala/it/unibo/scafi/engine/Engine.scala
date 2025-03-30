package it.unibo.scafi.engine

import it.unibo.scafi.engine.context.{ Context as BaseContext, ContextFactory }
import it.unibo.scafi.engine.network.{ Export, Import, Network as BaseNetwork }

/**
 * The engine is responsible for linking a context with a network and a program and handling the program execution for
 * every cycle.
 * @param network
 *   the network interface
 * @param factory
 *   the context factory, used to create a new context for every cycle
 * @param program
 *   the program to be executed every cycle
 * @tparam DeviceId
 *   the type of the device id
 * @tparam Result
 *   the type of the result of the program
 * @tparam Value
 *   the type of the value
 * @tparam Network
 *   the type of the network
 * @tparam Context
 *   the type of the context
 */
class Engine[
    DeviceId,
    Result,
    Value,
    Network <: BaseNetwork[DeviceId, Value],
    Context <: BaseContext[DeviceId, Value],
](
    private val network: Network,
    private val factory: ContextFactory[Network, Context],
    private val program: Context ?=> Result,
):

  private def round(): AggregateResult =
    val context: Context = factory.create(network)
    val result: Result = program(using context)
    val outMessages = context.outboundMessages
    network.send(outMessages)
    AggregateResult(result, context.inboundMessages, outMessages)

  /**
   * Executes a single cycle of the program.
   * @return
   *   the result of the program
   */
  def cycle(): Result = round().result

  /**
   * Executes the program until a condition is no longer satisfied.
   * @param predicate
   *   the condition to be checked at the end of every cycle
   * @return
   *   the result of the program after the last cycle
   */
  def cycleWhile(predicate: AggregateResult => Boolean): Result =
    var aggregateResult = round()
    while predicate(aggregateResult) do aggregateResult = round()
    aggregateResult.result

  /**
   * Compact representation of different aspects of an aggregate computation round/cycle.
   * @param result
   *   the result of the computation
   * @param inboundMessages
   *   the messages received from the network before the computation
   * @param outboundMessages
   *   the messages sent to the network after the computation
   */
  case class AggregateResult(
      result: Result,
      inboundMessages: Import[DeviceId, Value],
      outboundMessages: Export[DeviceId, Value],
  )
end Engine
