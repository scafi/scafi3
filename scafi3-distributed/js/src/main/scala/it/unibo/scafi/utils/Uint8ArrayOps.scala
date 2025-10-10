package it.unibo.scafi.utils

import scala.scalajs.js.typedarray.Uint8Array

object Uint8ArrayOps:
  given Conversion[Array[Byte], Uint8Array] = _.toUint8Array
  given Conversion[Uint8Array, Array[Byte]] = _.toByteArray

  extension (arr: Uint8Array)

    /**
     * Convert a `Uint8Array` to an array of bytes.
     * @return
     *   an equivalent Scala `Array[Byte]` representation of the `Uint8Array`.
     */
    def toByteArray: Array[Byte] =
      val result = new Array[Byte](arr.length)
      for i <- 0 until arr.length do result(i) = arr(i).toByte
      result

  extension (buffer: Array[Byte])

    /**
     * Convert an array of bytes to a `Uint8Array`.
     * @return
     *   an equivalent `Uint8Array` representation of the Scala `Array[Byte]`.
     */
    def toUint8Array: Uint8Array =
      val data = new Uint8Array(buffer.length)
      for i <- buffer.indices do data(i) = buffer(i)
      data
end Uint8ArrayOps
