package it.unibo.field4s.language.foundation

import it.unibo.field4s.UnitTest
import it.unibo.field4s.language.AggregateFoundation

trait DeviceAwareAggregateFoundationTests:
  this: AggregateFoundationTests & UnitTest =>

  override type A <: AggregateFoundation & FieldMock & DeviceMock

  def deviceAwareAggregateFoundation(): Unit =
    it should behave like aggregateFoundation()

    it should "be able to get the device id" in:
      lang.self shouldEqual 0

    it should "provide a field of aligned neighbours" in:
      lang.device.toIterable should contain(lang.self)
