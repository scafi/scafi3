package it.unibo.scafi.language.xc

/**
 * This trait defines the operations that can be performed on NValues.
 * @tparam SharedData
 *   the type of the SharedData.
 * @tparam DeviceId
 *   the type of the device id
 */
trait NeighborValuesOps[SharedData[_], DeviceId]:
  extension [A](sharedData: SharedData[A])
    /**
     * Returns the default value for unaligned devices.
     * @return
     *   the default value.
     */
    def default: A

    /**
     * Retrieves the value associated with the given device id. If the device is unaligned, returns the default value.
     * @param id
     *   the device id.
     * @return
     *   the value associated with the device id.
     * @throws NoSuchElementException
     *   if the device id is unaligned.
     */
    def apply(id: DeviceId): A

    /**
     * Retrieves the value associated with the given device id, if the device is aligned.
     * @param id
     *   the device id.
     * @return
     *   an option containing the value if the device is aligned, or None if it is unaligned.
     */
    def get(id: DeviceId): Option[A]

    /**
     * Retrieves a map of all device ids to their associated values.
     * @return
     *   a map of device ids to values.
     */
    def values: Map[DeviceId, A]

    private[xc] def set(id: DeviceId, value: A): SharedData[A]
  end extension
end NeighborValuesOps
