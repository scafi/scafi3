package it.unibo.scafi.runtime.network

import io.bullet.borer.Cbor
import it.unibo.scafi.message.Path
import it.unibo.scafi.runtime.network.CodableEnrichments.given_Codec_Path
import it.unibo.scafi.utils.InvocationCoordinate
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class CodableInstancesTest extends AnyWordSpec with should.Matchers:

  "A path" when:
    "composed of `InvocationCoordinate`s" should:
      "be encodable and decodable" in:
        val path = Path(InvocationCoordinate("branch/true", 0), InvocationCoordinate("exchange", 0))
        val encoded = Cbor.encode(path).toByteArray
        val decoded = Cbor.decode(encoded).to[Path].value
        decoded shouldBe path
