package it.unibo.field4s.language.semantics.exchange

import scala.collection.MapView

import it.unibo.field4s.language.semantics.exchange

/**
 * This trait defines the operations that can be performed on NValues.
 * @tparam NV
 *   the type of the NValues
 * @tparam DeviceId
 *   the type of the device id
 */
trait NValuesOps[NV[_], DeviceId]:

  extension [T](nv: NV[T])

    /**
     * @return
     *   the default value of the NValues
     */
    def default: T

    /**
     * @return
     *   the values associated with the device ids that override the default value
     */
    def values: MapView[DeviceId, T]

    /**
     * Maps the NValues to a new one with the value corresponding to the given device id set to the given value.
     * @param id
     *   the device id
     * @param value
     *   the new value
     * @return
     *   the new NValues
     */
    def set(id: DeviceId, value: T): NV[T]

    /**
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def get(id: DeviceId): T = nv.values.getOrElse(id, nv.default)

    /**
     * Alias for `get`.
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def apply(id: DeviceId): T = nv.get(id)
  end extension
end NValuesOps
