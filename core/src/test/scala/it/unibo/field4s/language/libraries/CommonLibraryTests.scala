package it.unibo.field4s.language.libraries

import it.unibo.field4s.UnitTest
import it.unibo.field4s.language.foundation.AggregateFoundationMock

import CommonLibrary.*

class CommonLibraryTests extends UnitTest:
  val lang: AggregateFoundationMock = AggregateFoundationMock()

  "mux" should "evaluate both branches" in:
    var ev = 0
    mux(true) { ev += 1 } { ev += 2 }
    ev shouldBe 3
    mux(false) { ev += 1 } { ev += 2 }
    ev shouldBe 6

  "self" should "return the current node id" in:
    self(using lang) shouldBe lang.self

  "device" should "return the device id field of neighbours" in:
    device(using lang).toList shouldBe lang.device.toList
