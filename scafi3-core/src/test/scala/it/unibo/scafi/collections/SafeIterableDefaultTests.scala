package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest

class SafeIterableDefaultTests extends UnitTest:

  "SafeIterable" should "provide a default implementation" in:
    SafeIterable(List(1, 2, 3)) shouldBe a[SafeIterable[Int]]
