package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ BinaryCodable, Path }
import it.unibo.scafi.utils.InvocationCoordinate

import io.bullet.borer.derivation.ArrayBasedCodecs.deriveCodec
import io.bullet.borer.*

/**
 * A collection of [[Codable]]s for various types used in the distributed runtime.
 */
object CodableInstances:

  private given Codec[InvocationCoordinate] = deriveCodec

  private given Codec[Any] = Codec(
    Encoder: (writer, path) =>
      path match
        case coordinate: InvocationCoordinate =>
          writer
            .writeArrayOpen(2)
            .write("InvocationCoordinate")
            .write(coordinate)
            .writeArrayClose()
        case _ => throw new IllegalArgumentException("Unsupported Path type: " + path.getClass.getName),
    Decoder: reader =>
      val unbounded = reader.readArrayOpen(2)
      val tag = reader.readString()
      val value = tag match
        case "InvocationCoordinate" => reader.read[InvocationCoordinate]()
        case _ => throw new IllegalArgumentException(s"Unsupported Path type: $tag")
      reader.readArrayClose(unbounded, value),
  )

  private given Codec[Path] = Codec(
    Encoder.forIndexedSeq[Any, IndexedSeq].contramap(identity),
    Decoder.forArray[Any].map(Path.apply),
  )

  given BinaryCodable[Path] = new BinaryCodable[Path]:
    override def encode(value: Path): Array[Byte] = Cbor.encode(value).toByteArray
    override def decode(data: Array[Byte]): Path = Cbor.decode(data).to[Path].value
end CodableInstances
