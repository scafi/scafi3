package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ BinaryCodable, Path, ValueTree }
import it.unibo.scafi.utils.InvocationCoordinate

import io.bullet.borer.{ Cbor, Codec, Decoder, Encoder }
import io.bullet.borer.derivation.ArrayBasedCodecs.deriveCodec

/**
 * A collection of [[Codable]]s for various types used in the distributed networking context.
 */
object CodableInstances:

  private given Codec[InvocationCoordinate] = deriveCodec

  private given Codec[Path] = Codec(
    Encoder.forIndexedSeq[InvocationCoordinate, IndexedSeq].contramap(identity),
    Decoder.forArray[InvocationCoordinate].map(coordinates => Path(coordinates.toIndexedSeq)),
  )

  /** A [[BinaryCodable]] for encoding and decoding [[Path]]s instances. */
  given pathCodable: BinaryCodable[Path] = new BinaryCodable[Path]:
    override def encode(value: Path): Array[Byte] = Cbor.encode(value).toByteArray
    override def decode(data: Array[Byte]): Path = Cbor.decode(data).to[Path].value

  /**
   * A [[BinaryCodable]] for encoding and decoding [[ValueTree]] instances whose values are already in binary form.
   * Values not in binary form will be discarded from the encoded output.
   */
  given valueTreeCodable: BinaryCodable[ValueTree] = new BinaryCodable[ValueTree]:
    override def encode(valueTree: ValueTree): Array[Byte] =
      val encodedPathsWithValues = valueTree.paths
        .flatMap: path =>
          try
            valueTree[Any](path) match
              case encodedValue: Array[Byte] => Some(path -> encodedValue)
              case _ => None
          catch
            case _: ValueTree.NoPathFoundException =>
              throw RuntimeException(s"Path: $path not found, this should not happen. Please report this.")
        .toMap
      Cbor.encode(encodedPathsWithValues).toByteArray

    override def decode(data: Array[Byte]): ValueTree =
      val pathsWithEncodedValues = Cbor.decode(data).to[Map[Path, Array[Byte]]].value
      ValueTree(pathsWithEncodedValues)

  /** A [[BinaryCodable]] for encoding and decoding tuples of two codable elements. */
  given [T1: BinaryCodable, T2: BinaryCodable]: BinaryCodable[(T1, T2)] = new BinaryCodable[(T1, T2)]:
    override def encode(value: (T1, T2)): Array[Byte] = Cbor
      .encode((summon[BinaryCodable[T1]].encode(value._1), summon[BinaryCodable[T2]].encode(value._2)))
      .toByteArray

    override def decode(data: Array[Byte]): (T1, T2) =
      val (rawT1, rawT2) = Cbor.decode(data).to[(Array[Byte], Array[Byte])].value
      summon[BinaryCodable[T1]].decode(rawT1) -> summon[BinaryCodable[T2]].decode(rawT2)

end CodableInstances
