package it.unibo.scafi.message

import it.unibo.scafi.utils.InvocationCoordinate

import io.bullet.borer.derivation.ArrayBasedCodecs.deriveCodec
import io.bullet.borer.{ Codec, Decoder, Encoder }

trait Path extends IndexedSeq[InvocationCoordinate]

object Path:
  def apply(tokens: InvocationCoordinate*): Path = PathImpl(tokens)

  private case class PathImpl(tokens: Seq[InvocationCoordinate]) extends Path:
    override def apply(i: Int): InvocationCoordinate = tokens(i)

    override def length: Int = tokens.length

    override def equals(o: Any): Boolean = o match
      case that: Path => this.toString == that.toString
      case _ => false

    override def hashCode: Int = tokens.hashCode()

    override def toString: String = tokens.mkString("Path(", ", ", ")")

  given CanEqual[Path, Path] = CanEqual.derived
  given CanEqual[Iterable[Path], Iterable[Path]] = CanEqual.derived

  given Codec[InvocationCoordinate] = deriveCodec
  given Encoder[Path] = Encoder.forIndexedSeq[InvocationCoordinate, IndexedSeq].contramap(identity)
  given Decoder[Path] = Decoder.forArray[InvocationCoordinate].map(Path.apply)
end Path
