package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.Path
import it.unibo.scafi.utils.InvocationCoordinate

import io.bullet.borer.derivation.ArrayBasedCodecs.deriveCodec
import io.bullet.borer.{ Codec, Decoder, Encoder }

object CodableEnrichments:

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
        case _ => throw new IllegalStateException("Unsupported Path type: " + path.getClass.getName),
    Decoder: reader =>
      val unbounded = reader.readArrayOpen(2)
      val tag = reader.readString()
      val value = tag match
        case "InvocationCoordinate" => reader.read[InvocationCoordinate]()
        case _ => throw new IllegalStateException(s"Unsupported Path type: $tag")
      reader.readArrayClose(unbounded, value),
  )

  given Codec[Path] = Codec(
    Encoder.forIndexedSeq[Any, IndexedSeq].contramap(identity),
    Decoder.forArray[Any].map(Path.apply),
  )
end CodableEnrichments
