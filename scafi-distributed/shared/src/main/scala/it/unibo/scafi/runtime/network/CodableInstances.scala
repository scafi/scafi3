package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ BinaryCodable, Path, ValueTree }
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
      val value = reader.readString() match
        case "InvocationCoordinate" => reader.read[InvocationCoordinate]()
        case tag => throw new IllegalArgumentException(s"Unsupported Path type: $tag")
      reader.readArrayClose(unbounded, value),
  )

  private given Codec[Path] = Codec(
    Encoder.forIndexedSeq[Any, IndexedSeq].contramap(identity),
    Decoder.forArray[Any].map(Path.apply),
  )

  /** A [[BinaryCodable]] for encoding and decoding [[Path]]s instances. */
  given pathCodable: BinaryCodable[Path] = new BinaryCodable[Path]:
    override def encode(value: Path): Array[Byte] = Cbor.encode(value).toByteArray
    override def decode(data: Array[Byte]): Path = Cbor.decode(data).to[Path].value

  /** A [[BinaryCodable]] for encoding and decoding [[ValueTree]] instances whose values are already in binary form. */
  given valueTreeCodable: BinaryCodable[ValueTree] = new BinaryCodable[ValueTree]:
    override def encode(valueTree: ValueTree): Array[Byte] =
      val encodedPathsWithValues = valueTree.paths
        .map: path =>
          try pathCodable.encode(path) -> valueTree[Array[Byte]](path)
          catch
            case _: ValueTree.NoPathFoundException =>
              throw RuntimeException(s"Path: $path not found, this should not happen. Please report this.")
        .toMap
      Cbor.encode(encodedPathsWithValues).toByteArray

    override def decode(data: Array[Byte]): ValueTree =
      val pathsWithValues = Cbor.decode(data).to[Map[Array[Byte], Array[Byte]]].value
      ValueTree(pathsWithValues.map(pathCodable.decode(_) -> _))

  /** A [[BinaryCodable]] for encoding and decoding tuples of two codable elements. */
  given [T1: BinaryCodable, T2: BinaryCodable]: BinaryCodable[(T1, T2)] = new BinaryCodable[(T1, T2)]:
    override def encode(value: (T1, T2)): Array[Byte] = Cbor
      .encode((summon[BinaryCodable[T1]].encode(value._1), summon[BinaryCodable[T2]].encode(value._2)))
      .toByteArray

    override def decode(data: Array[Byte]): (T1, T2) =
      val (rawT1, rawT2) = Cbor.decode(data).to[(Array[Byte], Array[Byte])].value
      summon[BinaryCodable[T1]].decode(rawT1) -> summon[BinaryCodable[T2]].decode(rawT2)

end CodableInstances
