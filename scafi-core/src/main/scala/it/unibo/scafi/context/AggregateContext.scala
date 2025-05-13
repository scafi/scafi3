package it.unibo.scafi.context

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.{ Export, Import }

trait AggregateContext:
  self: AggregateFoundation =>

  /**
   * Based on the current context, produces an [[Export]] to be sent to the neighbors.
   * @return
   *   the [[Export]] to be sent to the neighbors.
   */
  def exportFromOutboundMessages: Export[DeviceId]

  /**
   * Generates an [[Import]] based on the received neighbor messages.
   * @return
   *   the [[Import]] to be used to compute the next round.
   */
  def importFromInboundMessages: Import[DeviceId]

  /**
   * The known neighbors.
   * @return
   *   a collection of the known neighbors.
   */
  def neighbors: Iterable[DeviceId]

  override def localId: DeviceId
end AggregateContext
