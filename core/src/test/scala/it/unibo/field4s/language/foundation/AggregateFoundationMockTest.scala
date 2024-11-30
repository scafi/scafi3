package it.unibo.field4s.language.foundation

import it.unibo.field4s.UnitTest
import it.unibo.field4s.abstractions.{ AggregateTests, LiftableTests, MappableTests }
import it.unibo.field4s.collections.SafeIterableTests

class AggregateFoundationMockTest
    extends UnitTest
    with SafeIterableTests
    with DeviceAwareAggregateFoundationTests
    with AggregateFoundationTests
    with AggregateTests
    with LiftableTests
    with MappableTests:
  override type A = AggregateFoundationMock
  override val lang = new AggregateFoundationMock

  override def toIterable[A](fa: F[A]): Iterable[A] = fa.mockedValues

  "An aggregate foundation mock" should behave like deviceAwareAggregateFoundation()
