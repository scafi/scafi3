package it.unibo.scafi.engine.context.common

import it.unibo.scafi.collections.ValueTree
import it.unibo.scafi.engine.context.Context
import it.unibo.scafi.engine.network.Import

/**
 * Implements the semantics related to inbound messages coming from self and neighbours.
 */
trait InboundMessages:
  this: Stack & MessageManager & Context[DeviceId, ValueTree[InvocationCoordinate, Envelope]] =>

  private lazy val pathCaches = PreCachedPaths(inboundMessages)

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
  protected def neighbors: Set[DeviceId] = pathCaches.neighbors

  /**
   * @return
   *   the set of device ids of devices that are aligned with the current path, always including self
   */
  protected def alignedDevices: Set[DeviceId] =
    if currentPath.isEmpty then pathCaches.neighbors else pathCaches.alignedDevicesAt(currentPath)

  /**
   * @return
   *   the [[Map]]`[DeviceId, Envelope]` that contains the inbound values of devices that are aligned with the current
   *   path
   */
  protected def alignedMessages: Map[DeviceId, Envelope] = pathCaches.dataAt(currentPath)

  /**
   * @return
   *   the device id of the current device
   */
  protected def self: DeviceId

  private class PreCachedPaths(private val input: Import[DeviceId, ValueTree[InvocationCoordinate, Envelope]]):
    private lazy val cachedPaths: Map[IndexedSeq[InvocationCoordinate], Map[DeviceId, Envelope]] =
      input.foldLeft(Map.empty):
        case (accumulator, (deviceId, valueTree)) =>
          valueTree.prefixes.foldLeft(accumulator) { (accInner, path) =>
            accInner.updatedWith(path.toIndexedSeq):
              case Some(existing) => Some(existing + (deviceId -> valueTree(path)))
              case None => Some(Map(deviceId -> valueTree(path)))
          }

    lazy val neighbors: Set[DeviceId] = input.keySet + self

    def alignedDevicesAt(path: IndexedSeq[InvocationCoordinate]): Set[DeviceId] =
      cachedPaths.filter(_._1.startsWith(path)).values.flatMap(_.keySet).toSet + self

    def dataAt(path: IndexedSeq[InvocationCoordinate]): Map[DeviceId, Envelope] =
      cachedPaths.getOrElse(path, Map.empty)
end InboundMessages
