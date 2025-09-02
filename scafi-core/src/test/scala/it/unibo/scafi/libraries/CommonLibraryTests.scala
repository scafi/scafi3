package it.unibo.scafi.libraries

import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.libraries.CommonLibrary.{ device, localId, mux }

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class CommonLibraryTests extends AnyFlatSpecLike, should.Matchers:
  val lang: AggregateFoundationMock = AggregateFoundationMock()

  "mux" should "evaluate both branches" in:
    var ev = 0
    mux(true) { ev += 1 } { ev += 2 }
    ev shouldBe 3
    mux(false) { ev += 1 } { ev += 2 }
    ev shouldBe 6

  "localId" should "return the current node id" in:
    localId(using lang) shouldBe lang.localId

  "device" should "return the device id field of neighbours" in:
    device(using lang).toList shouldBe lang.device.toList
