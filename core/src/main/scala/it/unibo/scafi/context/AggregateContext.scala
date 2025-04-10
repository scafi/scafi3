package it.unibo.scafi.context

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.{Export, Import}

trait AggregateContext[DeviceId]:
  self: AggregateFoundation =>

  def exportFromOutboundMessages: Export[DeviceId]

  def importFromInboundMessages: Import[DeviceId]

  /**
   * The known neighbors.
   *
   * @return
   *   a collection of the known neighbors.
   */
  def neighbors: Iterable[DeviceId]

  def localId: DeviceId
