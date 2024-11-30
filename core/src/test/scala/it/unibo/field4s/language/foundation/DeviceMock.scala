package it.unibo.field4s.language.foundation

trait DeviceMock:
  this: DeviceAwareAggregateFoundation & AggregateFoundation & FieldMock =>

  override type DeviceId = Int

  override def self: DeviceId = 0

  override def device: AggregateValue[DeviceId] = mockField(Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
