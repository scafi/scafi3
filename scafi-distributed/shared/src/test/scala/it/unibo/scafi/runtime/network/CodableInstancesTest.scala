package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ Path, ValueTree }
import it.unibo.scafi.message.Codable.{ decode, encode }
import it.unibo.scafi.runtime.network.CodableInstances.given
import it.unibo.scafi.utils.InvocationCoordinate

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

import unsafeExceptions.canThrowAny

class CodableInstancesTest extends AnyWordSpec with should.Matchers:

  "A path" when:
    "composed of `InvocationCoordinate`s tokens" should:
      "be both encodable and decodable" in:
        val path = Path(InvocationCoordinate("branch/true", 0), InvocationCoordinate("exchange", 0))
        decode[Array[Byte], Path](encode(path)) shouldBe path

    "composed of other tokens" should:
      "fail to encode and decode" in:
        case class Unsupported(value: String)
        val unsupportedToken = Unsupported("branch/true")
        (the[Exception] thrownBy encode(Path(unsupportedToken))).getMessage should include(
          s"Unsupported Path token type ${unsupportedToken.getClass.getName}",
        )
        an[Exception] should be thrownBy decode[Array[Byte], Path]("Unsupported path".getBytes)

  "ValueTree" when:
    "composed of encoded values" should:
      "be both encodable and decodable" in:
        val path = Path(InvocationCoordinate("branch/true", 0), InvocationCoordinate("exchange", 0))
        val value = "value"
        val decoded = decode[Array[Byte], ValueTree](encode(ValueTree(Map(path -> value.getBytes))))
        new String(decoded[Array[Byte]](path)) shouldBe value

    "when composed of non-encoded values" should:
      "discard them during encoding" in:
        val trueBranch = Path(InvocationCoordinate("branch/true", 0), InvocationCoordinate("exchange", 0))
        val falseBranch = Path(InvocationCoordinate("branch/false", 0), InvocationCoordinate("exchange", 0))
        val trueBranchValue = "value1"
        val falseBranchValue = None
        val entries = Map(trueBranch -> trueBranchValue.getBytes, falseBranch -> falseBranchValue)
        val decoded = decode[Array[Byte], ValueTree](encode(ValueTree(entries)))
        new String(decoded[Array[Byte]](trueBranch)) shouldBe trueBranchValue
        decoded.get(falseBranch) shouldBe None
end CodableInstancesTest
