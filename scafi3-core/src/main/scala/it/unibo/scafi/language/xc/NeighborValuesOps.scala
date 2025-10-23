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
     * Maps the values of the SharedData using the provided function.
     * @param f
     *   the mapping function.
     * @tparam B
     *   the type of the mapped values.
     * @return
     *   a new SharedData with the mapped values.
     */
    def mapValues[B](f: A => B): SharedData[B]

    /**
     * Maps the values of this SharedData and another SharedData using the provided function. Assumes both SharedData
     * are aligned, i.e., they contain values for the same set of device ids.
     * @param other
     *   the other SharedData to map with.
     * @param f
     *   the mapping function that takes a value from this SharedData and a value from the other SharedData.
     * @tparam B
     *   the type of the values in the other SharedData.
     * @tparam C
     *   the type of the mapped values.
     * @return
     *   a new SharedData with the mapped values.
     */
    def alignedMap[B, C](other: SharedData[B])(f: (A, B) => C): SharedData[C]

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
