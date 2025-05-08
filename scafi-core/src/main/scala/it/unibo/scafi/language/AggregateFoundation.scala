package it.unibo.scafi.language

import it.unibo.scafi.collections.SafeIterable
import it.unibo.scafi.utils.SharedDataOps

import cats.Applicative

trait AggregateFoundation:
  /**
   * Abstract type representing the data shared with neighbors.
   */
  type SharedData[T] <: SafeIterable[T]

  /**
   * The type of device identifiers.
   */
  type DeviceId

  /**
   * Shared data values can be iterated also by ignoring the self-value.
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
  def localId: DeviceId

  /**
   * @return
   *   the aggregate value of device identifiers of aligned devices (including the current device)
   */
  def device: SharedData[DeviceId]

  /**
   * Given a token, performs a manual alignment calling the body function.
   * @param token
   *   to be used for alignment
   * @param body
   *   the code to align
   * @tparam T
   *   the type of the result
   * @return
   *   an aligned computation
   */
  def align[T](token: Any)(body: () => T): T
end AggregateFoundation
