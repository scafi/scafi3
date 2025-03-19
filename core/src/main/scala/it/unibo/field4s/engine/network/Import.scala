package it.unibo.field4s.engine.network

import it.unibo.field4s.engine.context.common.InvocationCoordinate

/**
 * An Import consists of a map of values, where the key is the device id and the value is the value tree corresponding.
 *
 * @tparam DeviceId
 *   the type of the device id
 * @tparam Value
 *   the type of the values in the tree
 */
type Import[DeviceId, Value] = ImportCachedData[DeviceId, Value]

trait ImportCachedData[DeviceId, Value]:
  def dataAt[Data](path: IndexedSeq[InvocationCoordinate]): Map[DeviceId, Data]
  val neighbors: Set[DeviceId]

object ImportCachedData:
  def apply[DeviceId, Value](neighborValues: Map[DeviceId, Value]): ImportCachedData[DeviceId, Value] = ???
