package it.unibo.scafi.context

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.{ Export, Import, ValueTree }

trait AggregateContext:
  self: AggregateFoundation =>

  /**
   * Based on the current context, produces an [[Export]] to be sent to the neighbors.
   * @return
   *   the [[Export]] to be sent to the neighbors.
   */
  def exportFromOutboundMessages: Export[DeviceId]

  /**
   * Based on the current context, produces a [[ValueTree]] containing self-messages to use in the next round.
   *
   * Unlike regular messages, self-messages are not transmitted over the network. Instead, they are propagated locally
   * and made available in the following round.
   * @return
   *   the [[ValueTree]] of self messages to be used in the next round.
   */
  def selfMessagesForNextRound: ValueTree

  /**
   * Generates an [[Import]] based on the received neighbor messages.
   * @return
   *   the [[Import]] to be used to compute the next round.
   */
  def importFromInboundMessages: Import[DeviceId]

  /**
   * Retrieves the [[ValueTree]] containing self-messages from the previous round used to update the local state.
   * @return
   *   the [[ValueTree]] of self-messages from the previous round.
   */
  def selfMessagesFromPreviousRound: ValueTree

  /**
   * The known neighbors.
   * @return
   *   a collection of the known neighbors.
   */
  def neighbors: Iterable[DeviceId]

  override def localId: DeviceId
end AggregateContext
