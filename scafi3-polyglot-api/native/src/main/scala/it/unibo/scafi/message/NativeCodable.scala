package it.unibo.scafi.message

import scala.scalanative.libc.stddef.size_t
import scala.scalanative.libc.stdlib
import scala.scalanative.unsafe.{ alloc, CSize, Ptr, Zone }
import scala.scalanative.unsafe.Size.intToSize

import it.unibo.scafi.nativebindings.structs.BinaryCodable as CBinaryCodable
import it.unibo.scafi.utils.CUtils.asVoidPtr
import it.unibo.scafi.utils.Uint8ArrayOps.{ toByteArray, toUint8Array }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeCodable:

  /** @return an instance of [[Codable]] for native `CBinaryCodable`. */
  given nativeCodable: Conversion[Ptr[CBinaryCodable], Codable[Ptr[CBinaryCodable], Any]] = value =>
    new Codable[Ptr[CBinaryCodable], Any]:
      override def encode(value: Ptr[CBinaryCodable]): Array[Byte] = Zone:
        val encodedSize = alloc[size_t]()
        val rawData = (!value).encode(value, encodedSize)
        val data = rawData.toByteArray(!encodedSize)
        stdlib.free(rawData)
        data

      override def decode(data: Any): Ptr[CBinaryCodable] =
        data match
          case bytes: Array[Byte] =>
            Zone((!value).decode(bytes.toUint8Array, bytes.length.toCSize)).asInstanceOf[Ptr[CBinaryCodable]]
          case _ => throw new IllegalArgumentException(s"$data is not a supported format.")

end NativeCodable
