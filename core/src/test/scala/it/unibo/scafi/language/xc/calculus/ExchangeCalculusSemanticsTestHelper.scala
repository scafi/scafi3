package it.unibo.scafi.language.xc.calculus

trait ExchangeCalculusSemanticsTestHelper:
  this: ExchangeCalculus =>
  override type DeviceId = Int
  def mockNValues[T](default: T, values: Map[DeviceId, T]): SharedData[T]
  def unalignedDeviceId: DeviceId
