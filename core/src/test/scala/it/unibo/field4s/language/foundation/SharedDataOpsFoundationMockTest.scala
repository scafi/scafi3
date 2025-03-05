package it.unibo.field4s.language.foundation

import it.unibo.field4s.UnitTest
import it.unibo.field4s.abstractions.AggregateTests
import it.unibo.field4s.collections.SafeIterableTests

class SharedDataOpsFoundationMockTest
    extends UnitTest
    with SafeIterableTests
    with DeviceAwareAggregateFoundationTests
    with AggregateFoundationTests
    with AggregateTests:
  override type A = AggregateFoundationMock
  override val lang = new AggregateFoundationMock

  "An aggregate foundation mock" should behave like deviceAwareAggregateFoundation()
