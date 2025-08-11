package it.unibo.scafi.message

import it.unibo.scafi.utils.InvocationCoordinate

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import io.bullet.borer.Cbor

class PathSerializationTest extends AnyFlatSpec with should.Matchers:

  "A path" should "be serialized and deserialized correctly" in:
    val originalPath = Path(InvocationCoordinate("exchange", 0))
    val serializedPath = Cbor.encode(originalPath).toByteArray
    val deserializedPath = Cbor.decode(serializedPath).to[Path].value
    deserializedPath shouldBe originalPath
    println(s"Original Path: $originalPath")
    println(s"Deserialized Path: $deserializedPath")
