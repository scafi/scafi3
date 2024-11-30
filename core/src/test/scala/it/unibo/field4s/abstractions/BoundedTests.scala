package it.unibo.field4s.abstractions

import it.unibo.field4s.UnitTest
import it.unibo.field4s.abstractions.boundaries.{ Bounded, UpperBounded }

trait BoundedTests:
  this: UnitTest =>

  def one[T: Numeric]: T = summon[Numeric[T]].one

  def numbers[T: Numeric]: TableFor1[T] =
    Table(
      "numbers",
      summon[Numeric[T]].zero,
      summon[Numeric[T]].one,
      summon[Numeric[T]].fromInt(10),
      summon[Numeric[T]].fromInt(Integer.MAX_VALUE),
      summon[Numeric[T]].fromInt(Integer.MIN_VALUE),
    )

  def upperBounded[T: Numeric: UpperBounded](): Unit =
    val upperBound = summon[UpperBounded[T]].upperBound

    it should "provide an upper bound for the type T" in:
      forAll(numbers[T]): n =>
        n should be < upperBound

    it should "provide a value that cannot be exceeded over" in:
      upperBound + one should equal(upperBound)

  def lowerBounded[T: Numeric: Bounded](): Unit =
    val lowerBound = summon[Bounded[T]].lowerBound

    it should "provide a lower bound for the type T" in:
      forAll(numbers[T]): n =>
        n should be > lowerBound

    it should "provide a value that cannot be exceeded under" in:
      lowerBound - one should equal(lowerBound)

  def bounded[T: Numeric: Bounded](): Unit =
    it should behave like upperBounded[T]()
    it should behave like lowerBounded[T]()
    val bounds = summon[Bounded[T]]
    it should "provide a lower bound that is less than the upper bound" in:
      bounds.lowerBound should be < bounds.upperBound
end BoundedTests
