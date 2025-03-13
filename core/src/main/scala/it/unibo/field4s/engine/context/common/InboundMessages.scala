package it.unibo.field4s.engine.context.common

import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.engine.context.Context
import it.unibo.field4s.engine.network.Import

/**
 * Implements the semantics related to inbound messages coming from self and neighbours.
 */
trait InboundMessages:
  this: Stack & MessageManager & Context[DeviceId, ValueTree[InvocationCoordinate, Envelope]] =>

  /**
   * The type of device ids.
   */
  type DeviceId

  /**
   * The type that wraps values stored and retrieved from value trees.
   */
  override type Envelope

  /**
   * @return
   *   the set of device ids of visible devices even if they are not aligned with the current path, always including
   *   self
   */
  protected def unalignedDevices: Set[DeviceId] = unalignedMessages.view.keys.toSet + self

  /**
   * @return
   *   the set of device ids of devices that are aligned with the current path, always including self
   */
  protected def alignedDevices: Set[DeviceId] = unalignedMessages
    .filter(currentPath.isEmpty || _._2.containsPrefix(currentPath))
    .keys
    .toSet
    + self

  /**
   * @return
   *   the [[Import]] that contains the inbound messages of visible devices even if they are not aligned with the
   *   current path, always including self
   */
  private def unalignedMessages: Import[DeviceId, ValueTree[InvocationCoordinate, Envelope]] = inboundMessages

  /**
   * @return
   *   the [[Map]]`[DeviceId, Envelope]` that contains the inbound values of devices that are aligned with the current
   *   path
   */
  protected def alignedMessages: Map[DeviceId, Envelope] = unalignedMessages
    .flatMap((id, valueTree) =>
      valueTree
        .get(currentPath.toList)
        .map(id -> _),
    )

  /**
   * @return
   *   the device id of the current device
   */
  protected def self: DeviceId
end InboundMessages
