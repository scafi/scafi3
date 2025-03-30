package it.unibo.scafi.language.exchange.semantics

trait ExchangeCalculusSemanticsTestHelper:
  this: ExchangeCalculusSemantics =>
  override type DeviceId = Int
  def mockNValues[T](default: T, values: Map[DeviceId, T]): SharedData[T]
  def unalignedDeviceId: DeviceId
