package it.unibo.scafi.language

import scala.collection.MapView

/**
 * This trait defines the operations that can be performed on [[SharedData]].
 * @tparam SharedData
 *   the type of the NeighboringValue
 * @tparam DeviceId
 *   the type of the device id
 */
trait ShareDataOps[SharedData[_], DeviceId]:

  extension [Value](sharedData: SharedData[Value])

    /**
     * @return
     *   the default value of the [[SharedData]]
     */
    def default: Value

    /**
     * @return
     *   the values associated with the device ids that override the default value
     */
    def values: MapView[DeviceId, Value]

    /**
     * Maps the [[SharedData]] to a new one with the value corresponding to the given device id set to the given value.
     * @param id
     *   the device id
     * @param value
     *   the new value
     * @return
     *   the new [[SharedData]]
     */
    def set(id: DeviceId, value: Value): SharedData[Value]

    /**
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def get(id: DeviceId): Value = sharedData.values.getOrElse(id, sharedData.default)

    /**
     * Alias for `get`.
     * @param id
     *   the device id
     * @return
     *   the value associated with the given device id, or the default value if the device id value is not overridden
     */
    def apply(id: DeviceId): Value = sharedData.get(id)
  end extension
end ShareDataOps
