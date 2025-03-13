package it.unibo.field4s.language

import it.unibo.field4s.abstractions.SharedDataOps
import it.unibo.field4s.collections.SafeIterable

import cats.Applicative

trait AggregateFoundation:
  type SharedData[T] <: SafeIterable[T]

  /**
   * The type of device identifiers.
   */
  type DeviceId

  /**
   * Shared data values can be iterated also by ignoring the self value.
   */
  given sharedDataOps: SharedDataOps[SharedData] = scala.compiletime.deferred

  /**
   * Aggregate values can be composed and mapped.
   */
  given sharedDataApplicative: Applicative[SharedData] = scala.compiletime.deferred

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
end AggregateFoundation
