package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest

trait ValueTreeFactoryTests:
  this: UnitTest =>

  def valueTreeFactory[VT[N, V] <: ValueTree[N, V]](factory: ValueTree.Factory[VT]): Unit =
    it should "allow creating empty value trees" in:
      factory.empty[Int, String].isEmpty shouldBe true

    it should "allow creating value trees by passing path value pairs" in:
      val tree = factory(List(1, 2, 3) -> "a", List(1, 2, 4) -> "b")
      tree.get(List(1, 2, 3)) shouldBe Some("a")

    it should "allow creating value trees by passing value trees" in:
      val tree = factory(factory(List(1, 2, 3) -> "a", List(1, 2, 4) -> "b"), factory(List(1, 2, 5) -> "c"))
      tree should contain theSameElementsAs List(List(1, 2, 3) -> "a", List(1, 2, 4) -> "b", List(1, 2, 5) -> "c")
