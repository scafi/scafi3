package it.unibo.scafi.message

import scala.scalanative.libc.stddef.size_t
import scala.scalanative.unsafe.{ alloc, fromCString, CSize, Ptr, Zone }
import scala.scalanative.unsafe.Size.intToSize

import it.unibo.scafi.message.UniversalCodable
import it.unibo.scafi.nativebindings.structs.BinaryCodable as CBinaryCodable
import it.unibo.scafi.utils.CUtils.asVoidPtr
import it.unibo.scafi.utils.Uint8ArrayOps.{ toByteArray, toUint8Array }

import io.bullet.borer.{ Cbor, Codec }
import io.bullet.borer.derivation.ArrayBasedCodecs.deriveCodec

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeBinaryCodable:

  private enum Format derives CanEqual:
    case Binary, String

  private given Codec[Format] = deriveCodec[Format]

  given nativeBinaryCodable: UniversalCodable[Ptr[CBinaryCodable], Array[Byte]] =
    new UniversalCodable[Ptr[CBinaryCodable], Array[Byte]]:
      private var registry = NativeCodablesRegistry.forStringId()

      override def register(value: Ptr[CBinaryCodable]): Unit = registry = registry.register(value)

      override def encode(value: Ptr[CBinaryCodable]): Array[Byte] = Zone:
        val typeName = fromCString((!value).type_name)
        val encodedSize = alloc[size_t]()
        val rawData = (!value).encode(value, encodedSize)
        val data = rawData.toByteArray(!encodedSize)
        Cbor.encode(typeName, Format.Binary, data).toByteArray

      override def decode(bytes: Array[Byte]): Ptr[CBinaryCodable] = Zone:
        val (typeName, format, encodedData) = Cbor.decode(bytes).to[(String, Format, Array[Byte])].value
        registry.get(typeName) match
          case Some(value) =>
            format match
              case Format.Binary =>
                (!value).decode(encodedData.toUint8Array, encodedData.length.toCSize).asInstanceOf[Ptr[CBinaryCodable]]
              case Format.String => throw new IllegalStateException("String format not yet supported in native.")
          case None => throw new IllegalStateException(s"Unknow type: $typeName. This should not happen. Report this.")

end NativeBinaryCodable
