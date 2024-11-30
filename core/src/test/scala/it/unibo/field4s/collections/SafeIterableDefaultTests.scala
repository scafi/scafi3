package it.unibo.field4s.collections

import it.unibo.field4s.UnitTest

class SafeIterableDefaultTests extends UnitTest:

  "SafeIterable" should "provide a default implementation" in:
    SafeIterable(List(1, 2, 3)) shouldBe a[SafeIterable[Int]]
