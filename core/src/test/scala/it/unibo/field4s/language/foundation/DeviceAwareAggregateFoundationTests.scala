package it.unibo.field4s.language.foundation

import it.unibo.field4s.UnitTest

trait DeviceAwareAggregateFoundationTests:
  this: AggregateFoundationTests & UnitTest =>

  override type A <: DeviceAwareAggregateFoundation & AggregateFoundation & FieldMock & DeviceMock

  def deviceAwareAggregateFoundation(): Unit =
    it should behave like aggregateFoundation()

    it should "be able to get the device id" in:
      lang.self shouldEqual 0

    it should "provide a field of aligned neighbours" in:
      lang.device.toIterable should contain(lang.self)
