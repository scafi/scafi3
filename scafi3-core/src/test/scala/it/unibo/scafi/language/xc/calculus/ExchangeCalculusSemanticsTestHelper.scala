package it.unibo.scafi.language.xc.calculus

trait ExchangeCalculusSemanticsTestHelper:
  this: ExchangeCalculus =>
  override type DeviceId = Int
  def mockSharedData[T](default: T, device: Set[DeviceId], values: Map[DeviceId, T]): SharedData[T]
  def unalignedDeviceId: DeviceId
