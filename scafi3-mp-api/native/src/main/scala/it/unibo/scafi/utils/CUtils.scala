package it.unibo.scafi.utils

import scala.reflect.ClassTag
import scala.scalanative.libc.stdlib.malloc
import scala.scalanative.posix.string.strdup
import scala.scalanative.unsafe.{ sizeOf, toCString, CString, Ptr, Zone }

/** A bunch of utilities for C interop. */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
object CUtils:

  /**
   * Allocates memory for a type `T`, returning a pointer to it.
   * @tparam T
   *   the type of the pointer
   * @return
   *   a [[Ptr]] pointing to the allocated memory
   */
  inline def freshPointer[T]: Ptr[T] = freshPointer[T](1)

  /**
   * Allocates memory for a type `T`, multiplied by a factor, returning a pointer to it.
   * @param factor
   *   the factor to multiply the size of the pointer by
   * @tparam T
   *   the type of the pointer
   * @return
   *   a [[Ptr]] pointing to the allocated memory
   */
  inline def freshPointer[T](factor: Int): Ptr[T] =
    requireNonNull(malloc(sizeOf[T] * factor).asInstanceOf[Ptr[T]])

  /**
   * Check whether an object is null, throwing a [[NullPointerException]] if it is.
   * @param obj
   *   the object to check
   * @tparam T
   *   the type of the object
   * @return
   *   the object if it is not null
   */
  inline def requireNonNull[T](obj: T): T =
    if obj.asInstanceOf[AnyRef] == null then throw new NullPointerException(s"Object $obj is null") else obj

  extension (str: String)
    /**
     * Converts a Scala String to a C-style string (null-terminated array of characters) that is not confined to a zone.
     * @return
     *   a pointer to a null-terminated array of characters representing the C string
     * @note
     *   the pointer is allocated and must be manually freed by the caller.
     */
    def toUnconfinedCString: CString = Zone(strdup(toCString(str)))

  /** In C a pointer to any type can be treated as a `void*`. */
  given asVoidPtr[T]: Conversion[Ptr[T], Ptr[Byte]] = _.asInstanceOf[Ptr[Byte]]

end CUtils
