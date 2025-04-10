package it.unibo.scafi.message

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.utils.Stack

import scala.collection.mutable

trait OutboundMessage:
  self: Stack & AggregateContext[DeviceId] =>
  
  type DeviceId

  private val registeredMessages = mutable.Map.empty[Path[InvocationCoordinate], MapWithDefault[DeviceId, Any]]

  /**
   * Write a value at the current path with a [[default]] and possible [[overrides]].
   * @param default
   *   the default value for unknown neighbors.
   * @param overrides
   *   specific values for each known neighbor.
   * @tparam Value
   *   the type of the value to be written.
   */
  protected def writeValue[Value](default: Value, overrides: Map[DeviceId, Value]): Unit =
    registeredMessages.update(Path(currentPath*), MapWithDefault(overrides, default))

  override def exportFromOutboundMessages: Export[DeviceId] =
    val messages = mutable.Map.empty[DeviceId, ValueTree].withDefaultValue(ValueTree.empty)
    var default = ValueTree.empty
    for (path, messagesMap) <- registeredMessages do
      for deviceId <- neighbors do
        val currentValueTree = messages(deviceId)
        val updatedValueTree = currentValueTree.update(path, messagesMap(deviceId))
        messages.update(deviceId, updatedValueTree)
        default = default.update(path, messagesMap.default)
    Export(default, messages.toMap)
end OutboundMessage
