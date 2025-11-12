package it.unibo.scafi.types

import java.util.concurrent.atomic.AtomicLong

import scala.collection.mutable
import scala.language.unsafeNulls
import scala.scalanative.unsafe.{ exported, CFuncPtr1, CSize, CVoidPtr, Ptr, UnsafeRichLong }
import scala.scalanative.unsafe.Size.intToSize

import it.unibo.scafi.message.HashInstances.{ cEqHash, catsSyntaxEq }
import it.unibo.scafi.nativebindings.structs.Eq as CEq

/**
 * A facade around a mutable map to be used in C/C++ code via Scala Native. From the C/C++ side, the map is represented
 * as an opaque pointer (`void*`) that acts as a handle to identify the map instance.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
object CMap:

  private val activeRefs = mutable.Map.empty[Long, mutable.Map[?, ?]]
  private val nextHandle = AtomicLong(0L)

  def apply[Key, Value](map: mutable.Map[Key, Value]): Ptr[Byte] =
    val handle = nextHandle.incrementAndGet()
    synchronized(activeRefs.update(handle, map))
    handle.toPtr[Byte]

  def of[Key, Value](ptr: Ptr[Byte]): mutable.Map[Key, Value] =
    val handle = ptr.toLong
    synchronized(activeRefs(handle)).asInstanceOf[mutable.Map[Key, Value]]

  @exported("map_empty")
  def empty: Ptr[Byte] = apply(mutable.Map.empty[CVoidPtr, CVoidPtr])

  @exported("map_put")
  def put(handle: Ptr[Byte], key: Ptr[CEq], value: CVoidPtr): CVoidPtr =
    val scalaMap = of[Ptr[CEq], CVoidPtr](handle)
    val (keyToUpdate, oldValue) = scalaMap.find { case (k, _) => k === key }.fold(key -> null)(identity)
    scalaMap.update(keyToUpdate, value)
    oldValue

  @exported("map_get")
  def get(handle: Ptr[Byte], key: Ptr[CEq]): CVoidPtr =
    of[Ptr[CEq], CVoidPtr](handle).find { case (k, _) => k === key }.map(_._2).orNull

  @exported("map_foreach")
  def foreach(handle: Ptr[Byte], f: CFuncPtr1[CVoidPtr, Unit]): Unit =
    of[Ptr[CEq], CVoidPtr](handle).foreach { case (_, v) => f(v) }

  @exported("map_size")
  def size(handle: Ptr[Byte]): CSize = of(handle).size.toCSize

  @exported("map_free")
  def free(handle: Ptr[Byte]): Unit = synchronized(activeRefs.remove(handle.toLong)): Unit
end CMap
