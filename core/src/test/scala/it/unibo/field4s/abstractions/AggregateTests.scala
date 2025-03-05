package it.unibo.field4s.abstractions

import it.unibo.field4s.UnitTest
import it.unibo.field4s.collections.{ SafeIterable, SafeIterableTests }

trait AggregateTests:
  this: UnitTest & SafeIterableTests =>

  def aggregate[A, F[X] <: SafeIterable[X]: SharedDataOps](agg: F[A])(using CanEqual[A, A]): Unit =
    it should behave like safeIterable(agg)
    it should "provide an iterable without self as extension" in:
      agg.withoutSelf.toSet should contain theSameElementsAs agg.toSet - agg.onlySelf
    it should "provide a value for self as extension" in:
      agg.toSet should contain(agg.onlySelf)
    "Neighbouring aggregate" should behave like safeIterable(agg.withoutSelf)
