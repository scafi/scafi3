package it.unibo.scafi.utils

import scala.scalanative.libc.stddef.size_t
import scala.scalanative.posix.inttypes.uint8_t
import scala.scalanative.unsafe.{ alloc, Ptr, Zone }
import scala.scalanative.unsafe.Size.byteToSize
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.utils.CUtils.freshPointer

object Uint8ArrayOps:

  extension (bytes: Array[Byte])
    /**
     * Convert an array of bytes to a pointer to `uint8_t`.
     * @return
     *   a pointer to `uint8_t` representing the array of bytes
     * @note
     *   the pointer is allocated in the given zone and will be automatically freed when the zone is closed. Accessing
     *   it outside the zone will lead to undefined behavior.
     */
    def toUint8Array(using Zone): Ptr[uint8_t] = alloc[uint8_t](bytes.length).tap(writeTo)

    /**
     * Convert an array of bytes to a pointer to `uint8_t` that is not confined to a zone.
     * @return
     *   a pointer to `uint8_t` representing the array of bytes
     * @note
     *   the pointer is allocated using `malloc` and must be manually freed by the caller.
     */
    def toUnconfinedUint8Array: Ptr[uint8_t] = freshPointer[uint8_t](bytes.length).tap(writeTo)

    private def writeTo(ptr: Ptr[uint8_t]): Unit = for i <- bytes.indices do !(ptr + i) = bytes(i).toUByte
  end extension

  extension (ptr: Ptr[uint8_t])

    /**
     * Convert a pointer to `uint8_t` to an array of bytes.
     * @param size
     *   the size of the array to convert to
     * @return
     *   an array of bytes representing the pointer to `uint8_t`
     * @note
     *   the pointer is not freed, as it is assumed to be managed elsewhere. It is the caller's responsibility to ensure
     *   that the pointer is valid and points to a memory region of at least `size` bytes.
     */
    def toByteArray(size: size_t): Array[Byte] =
      val array = new Array[Byte](size.toInt)
      for i <- 0 until size.toInt do array(i) = (!(ptr + i)).toByte
      array
end Uint8ArrayOps
