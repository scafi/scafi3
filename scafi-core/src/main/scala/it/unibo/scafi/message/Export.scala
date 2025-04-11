package it.unibo.scafi.message

/**
 * Represents the produced [[ValueTree]]s for each [[DeviceId]].
 * @tparam DeviceId
 *   the type of the deviceId of neighbor devices.
 */
trait Export[DeviceId]:
  /**
   * Retrieves the [[ValueTree]] associated to the given [[deviceId]]. If the [[deviceId]] is not present in the
   * [[Export]], it returns the default [[ValueTree]].
   *
   * @param deviceId
   *   the id of the device to retrieve the [[ValueTree]] for.
   * @return
   *   the [[ValueTree]] associated to the given [[deviceId]] or the default [[ValueTree]] if the [[deviceId]] is not
   *   present.
   */
  def apply(deviceId: DeviceId): ValueTree

object Export:
  /**
   * Creates an [[Export]] from a default [[ValueTree]] and a map of [[ValueTree]]s for each [[DeviceId]].
   * @param default
   *   the default [[ValueTree]] to use if the [[deviceId]] is not present in the map.
   * @param overrides
   *   the map of [[ValueTree]]s for each [[DeviceId]].
   * @tparam DeviceId
   *   the type of the deviceId of neighbor devices.
   * @return
   *   an [[Export]] that retrieves the [[ValueTree]] associated to the given [[deviceId]] or the default.
   */
  def apply[DeviceId](default: ValueTree, overrides: Map[DeviceId, ValueTree]): Export[DeviceId] =
    deviceId => overrides.getOrElse(deviceId, default)
