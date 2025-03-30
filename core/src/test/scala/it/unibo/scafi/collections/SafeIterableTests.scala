package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest
import it.unibo.scafi.abstractions.boundaries.Bounded

trait SafeIterableTests:
  this: UnitTest =>

  def safeIterable[A](sut: SafeIterable[A])(using CanEqual[A, A]): Unit =
    it should "allow to be converted to standard iterator" in:
      sut should not be an[Iterable[A]]
      sut.toIterable shouldBe an[Iterable[A]]
      sut.toIterable should contain theSameElementsAs sut.toList

    it should "be printed like an iterator" in:
      sut.toString shouldBe sut.toIterable.toString

    it should "be hashed like an iterator" in:
      sut.hashCode shouldBe sut.toIterable.hashCode

    it should "export folding" in:
      sut.foldLeft(List[A]())(_ :+ _) shouldBe sut.toIterable.foldLeft(List[A]())(_ :+ _)
      sut.foldRight(List[A]())(_ :: _) shouldBe sut.toIterable.foldRight(List[A]())(_ :: _)

  def safeIterableOfBoundedType[A: Ordering: Bounded](nonEmpty: SafeIterable[A], empty: SafeIterable[A])(using
      CanEqual[A, A],
  ): Unit =
    it should behave like safeIterable(nonEmpty)
    it should behave like safeIterable(empty)

    it should "allow to compute min only with bounds" in:
      nonEmpty.min shouldBe nonEmpty.toIterable.min
      empty.min shouldBe summon[Bounded[A]].upperBound

    it should "allow to compute max only with bounds" in:
      nonEmpty.max shouldBe nonEmpty.toIterable.max
      empty.max shouldBe summon[Bounded[A]].lowerBound
end SafeIterableTests
