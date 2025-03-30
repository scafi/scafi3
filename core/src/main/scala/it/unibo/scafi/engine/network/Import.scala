package it.unibo.scafi.engine.network

/**
 * An Import consists of a map of values, where the key is the device id and the value is the value tree corresponding.
 *
 * @tparam DeviceId
 *   the type of the device id
 * @tparam Value
 *   the type of the values in the tree
 */
type Import[DeviceId, Value] = Map[DeviceId, Value]
