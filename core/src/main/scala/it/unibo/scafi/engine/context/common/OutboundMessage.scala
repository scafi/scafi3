package it.unibo.scafi.engine.context.common

import scala.collection.{ mutable, MapView }

import it.unibo.scafi.collections.{ MapWithDefault, ValueTree }
import it.unibo.scafi.engine.context.Context
import it.unibo.scafi.engine.network.Export
import it.unibo.scafi.engine.path.Path

/**
 * Implements the semantics related to outbound messages directed to self and neighbours.
 */
trait OutboundMessage:
  this: Stack & MessageManager & Context[DeviceId, ValueTree[InvocationCoordinate, Envelope]] =>

  /**
   * The type of device ids.
   */
  type DeviceId

  /**
   * The type that wraps values stored and retrieved from value trees.
   */
  override type Envelope

  override def outboundMessages: Export[DeviceId, ValueTree[InvocationCoordinate, Envelope]] =
    var messages: Map[DeviceId, ValueTree[InvocationCoordinate, Envelope]] = Map
      .empty[DeviceId, ValueTree[InvocationCoordinate, Envelope]]
      .withDefaultValue(ValueTree.empty)
    var default: ValueTree[InvocationCoordinate, Envelope] = ValueTree.empty
    for (path, messageMap) <- sentMessages do
      for deviceId <- neighbors do
        messages = messages.updated(deviceId, messages(deviceId).update(path, messageMap(deviceId)))
      default = default.update(path, messageMap.default)
    MapWithDefault(messages, default)

  private val sentMessages: mutable.Map[Path[InvocationCoordinate], MapWithDefault[DeviceId, Envelope]] =
    mutable.Map.empty

  /**
   * Adds a message to the outbound message box, located at the current path.
   * @param messages
   *   the messages to add
   * @param default
   *   the default message to add for unaligned devices
   * @tparam T
   *   the type of the messages
   */
  protected def sendMessages[T](messages: MapView[DeviceId, T], default: T): Unit =
    sentMessages.update(
      currentPath.toList,
      MapWithDefault(
        messages.mapValues(close).toMap,
        close(default),
      ),
    )

  /**
   * @return
   *   the set of device ids of visible devices even if they are not aligned with the current path, always including
   *   self
   */
  protected def neighbors: Set[DeviceId]
end OutboundMessage
