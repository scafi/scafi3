package it.unibo.scafi.message

/**
 * Represents the produced [[ValueTree]]s for each [[DeviceId]].
 * @tparam DeviceId
 *   the type of the deviceId of neighbor devices.
 */
trait Export[DeviceId]:
  /**
   * The [[ValueTree]] produced for the given deviceId. A possible [[NoDeviceIdException]] is thrown if the deviceId is
   * not present in the [[Export]].
   * @param deviceId
   *   the deviceId for which the [[ValueTree]] is produced.
   * @return
   *   the [[ValueTree]] produced for the given deviceId.
   */
  def apply(deviceId: DeviceId): ValueTree
end Export

object Export:
  def apply[DeviceId](default: ValueTree, overrides: Map[DeviceId, ValueTree]): Export[DeviceId] = ???
