package it.unibo.field4s.language.foundation

trait DeviceAwareAggregateFoundation:
  this: AggregateFoundation =>

  /**
   * The type of device identifiers.
   */
  type DeviceId

  /**
   * Device identifiers must be equatable.
   */
  given idEquality: CanEqual[DeviceId, DeviceId] = CanEqual.derived

  /**
   * @return
   *   the device identifier of the current device
   */
  def self: DeviceId

  /**
   * @return
   *   the aggregate value of device identifiers of aligned devices (including the current device)
   */
  def device: SharedData[DeviceId]

end DeviceAwareAggregateFoundation
