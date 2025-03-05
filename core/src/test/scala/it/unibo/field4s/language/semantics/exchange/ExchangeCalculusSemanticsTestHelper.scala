package it.unibo.field4s.language.semantics.exchange

trait ExchangeCalculusSemanticsTestHelper:
  this: ExchangeCalculusSemantics =>
  override type DeviceId = Int
  def mockNValues[T](default: T, values: Map[DeviceId, T]): SharedData[T]
  def unalignedDeviceId: DeviceId
