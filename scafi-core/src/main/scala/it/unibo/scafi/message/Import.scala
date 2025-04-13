package it.unibo.scafi.message

/**
 * Represents all the messages received from neighbors for a specific device. It has no concept of default values:
 * meaning that if a neighbor is not present in the [[Import]], that the neighbor is not a neighbor anymore.
 *
 * @tparam DeviceId
 *   the type of the deviceId of neighbor devices.
 */
trait Import[DeviceId] extends Iterable[(DeviceId, ValueTree)]:
  /**
   * @return
   *   the [[Set]] of neighbors' [[DeviceId]]s.S
   */
  def neighbors: Set[DeviceId]

object Import:
  /**
   * Creates an [[Import]] from a map of [[ValueTree]]s for each [[DeviceId]]. The [[ValueTree]]s are the messages
   * received from the neighbors.
   * @param messages
   *   the map of [[ValueTree]]s for each [[DeviceId]].
   * @tparam DeviceId
   *   the type of the deviceId of neighbor devices.
   * @return
   *   an [[Import]] containing the [[ValueTree]] according to the [[messages]].
   */
  def apply[DeviceId](messages: Map[DeviceId, ValueTree]): Import[DeviceId] = new Import[DeviceId]:
    override def neighbors: Set[DeviceId] = messages.keySet
    override def iterator: Iterator[(DeviceId, ValueTree)] = messages.iterator

  /**
   * Creates an empty [[Import]]. Typically used to create an [[Import]] when no neighbors are present.
   * @tparam DeviceId
   *   the type of the deviceId of neighbor devices.
   * @return
   *   an empty [[Import]].
   */
  def empty[DeviceId]: Import[DeviceId] = new Import[DeviceId]:
    override def neighbors: Set[DeviceId] = Set.empty
    override def iterator: Iterator[(DeviceId, ValueTree)] = Iterator.empty
end Import
