package it.unibo.scafi.runtime.context

import it.unibo.scafi.message.{ Export, Import }

trait AggregateContext[DeviceId]:
  def exportFromOutboundMessages: Export[DeviceId]

  def importFromInboundMessages: Import[DeviceId]

  /**
   * The known neighbors.
   *
   * @return
   *   a collection of the known neighbors.
   */
  def neighbors: Iterable[DeviceId]

  /**
   * The ID of the local device.
   * @return
   *   the ID of the local device.
   */
  def localId: DeviceId
