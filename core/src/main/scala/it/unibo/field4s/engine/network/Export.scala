package it.unibo.field4s.engine.network

import it.unibo.field4s.collections.MapWithDefault

/**
 * An Export consists of a map of values, where the key is the device id and the value is the value tree corresponding.
 * The default value provides a tree to send to a device that is not present in the map.
 * @tparam DeviceId
 *   the type of the device id
 * @tparam Value
 *   the type of the values in the tree
 */
type Export[DeviceId, Value] = MapWithDefault[DeviceId, Value]
