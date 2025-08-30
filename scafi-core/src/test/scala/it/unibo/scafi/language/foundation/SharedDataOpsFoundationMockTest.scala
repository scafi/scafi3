package it.unibo.scafi.language.foundation

import it.unibo.scafi.UnitTest
import it.unibo.scafi.abstractions.AggregateTests
import it.unibo.scafi.collections.SafeIterableTests

class SharedDataOpsFoundationMockTest
    extends UnitTest
    with SafeIterableTests
    with DeviceAwareAggregateFoundationTests
    with AggregateFoundationTests
    with AggregateTests:
  override type A = AggregateFoundationMock
  override val lang = new AggregateFoundationMock

  "An aggregate foundation mock" should behave like deviceAwareAggregateFoundation()
