package it.unibo.scafi.types

import scala.collection.mutable
import scala.language.unsafeNulls
import scala.scalanative.unsafe.{ exported, CFuncPtr2, CSize, CVoidPtr }
import scala.scalanative.unsafe.Size.intToSize
import scala.util.chaining.scalaUtilChainingOps

/**
 * A simple wrapper around a mutable map to be used in C/C++ code via Scala Native. The class exports some of the
 * underlying map methods to be directly callable from C/C++.
 * @see
 *   companion object for exported functions callable from C/C++.
 */
class CMap[Key, Value] private (underlying: mutable.Map[Key, Value]):
  export underlying.{ size, foreach, get, update }

  def toScalaMap: Map[Key, Value] = underlying.view.toMap

object CMap:

  /* NOTE: Scala objects are tracked by Garbage Collector. To avoid them being collected while still in use from C
   * side (the collector cannot be aware of), we keep a reference to them in this set. */
  private val activeRefs = mutable.Set.empty[CMap[?, ?]]

  def empty[Key, Value]: CMap[Key, Value] = apply(mutable.Map.empty)

  def apply[Key, Value](underlying: mutable.Map[Key, Value]): CMap[Key, Value] =
    new CMap(underlying).tap(synchronized(activeRefs.add))

  @exported("map_empty")
  def void(): CMap[CVoidPtr, CVoidPtr] = new CMap(mutable.Map.empty).tap(synchronized(activeRefs.add))

  @exported("map_put")
  def put(map: CMap[CVoidPtr, CVoidPtr], key: CVoidPtr, value: CVoidPtr): Unit = map.update(key, value)

  @exported("map_get")
  def get(map: CMap[CVoidPtr, CVoidPtr], key: CVoidPtr): CVoidPtr = map.get(key).orNull

  @exported("map_size")
  def size(map: CMap[CVoidPtr, CVoidPtr]): CSize = map.size.toCSize

  @exported("map_foreach")
  def foreach(map: CMap[CVoidPtr, CVoidPtr], f: CFuncPtr2[CVoidPtr, CVoidPtr, Unit]): Unit = map.foreach(f.apply)

  @exported("map_free")
  def free(map: CMap[CVoidPtr, CVoidPtr]): Unit = synchronized(activeRefs.remove(map): Unit)
end CMap
