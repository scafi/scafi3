package it.unibo.field4s.language.semantics.exchange

import scala.collection.MapView

import it.unibo.field4s.language.semantics.exchange

/**
 * This trait defines the operations that can be performed on NValues.
 * @tparam NV
 *   the type of the NeighboringValue
 * @tparam DeviceId
 *   the type of the device id
 */
trait NeighboringValue[NV[_], DeviceId]:

  extension [Value](neighboringValues: NV[Value])

    /**
     * @return
     *   the default value of the NValues
     */
    def default: Value

    /**
     * @return
     *   the values associated with the device ids that override the default value
     */
    def values: MapView[DeviceId, Value]

    /**
     * Maps the NValues to a new one with the value corresponding to the given device id set to the given value.
     * @param id
     *   the device id
     * @param value
     *   the new value
     * @return
     *   the new NValues
     */
    def set(id: DeviceId, value: Value): NV[Value]

    /**
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def get(id: DeviceId): Value = neighboringValues.values.getOrElse(id, neighboringValues.default)

    /**
     * Alias for `get`.
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def apply(id: DeviceId): Value = neighboringValues.get(id)
  end extension
end NeighboringValue
