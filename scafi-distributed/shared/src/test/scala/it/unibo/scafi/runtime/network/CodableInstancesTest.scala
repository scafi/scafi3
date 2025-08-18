package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.Path
import it.unibo.scafi.runtime.network.CodableInstances.given
import it.unibo.scafi.utils.InvocationCoordinate
import it.unibo.scafi.message.Codable.*

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class CodableInstancesTest extends AnyWordSpec with should.Matchers:

  "A path" when:
    "composed of `InvocationCoordinate`s tokens" should:
      "be both encodable and decodable" in:
        val path = Path(InvocationCoordinate("branch/true", 0), InvocationCoordinate("exchange", 0))
        decode[Array[Byte], Path](encode(path)) shouldBe path

    "composed of other tokens" should:
      "fail to encode and decode" in:
        case class Unsupported(value: String)
        val path = Path(Unsupported("branch/true"), Unsupported("exchange"))
        an[Throwable] should be thrownBy encode(path)
        an[Throwable] should be thrownBy decode[Array[Byte], Path](encode(path))
