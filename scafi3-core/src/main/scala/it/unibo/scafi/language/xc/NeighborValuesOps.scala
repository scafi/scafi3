package it.unibo.scafi.language.xc

/**
 * This trait defines the operations that can be performed on NValues.
 * @tparam SharedData
 *   the type of the NeighboringValue
 * @tparam DeviceId
 *   the type of the device id
 */
trait NeighborValuesOps[SharedData[_], DeviceId]:
  extension [A](sharedData: SharedData[A])
    def default: A

    def neighbors: Map[DeviceId, A]

    def mapValues[B](f: A => B): SharedData[B]

    def alignedMap[B, C](other: SharedData[B])(f: (A, B) => C): SharedData[C]

    def apply(id: DeviceId): A

    def values: Map[DeviceId, A]

    private[xc] def set(id: DeviceId, value: A): SharedData[A]
  end extension
end NeighborValuesOps
