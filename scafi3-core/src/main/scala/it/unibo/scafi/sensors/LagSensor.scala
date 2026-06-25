package it.unibo.scafi.sensors

import it.unibo.scafi.language.AggregateFoundation

/**
 * If an aggregate foundation implements this trait, it provides the communication lag perceived from each neighbour.
 * @tparam Lag
 *   the type used to represent lag (e.g. [[scala.concurrent.duration.FiniteDuration]])
 */
trait LagSensor[Lag]:
  this: AggregateFoundation =>

  /**
   * @return
   *   an aggregate value holding the lag from each neighbour (and self).
   */
  def senseLag: SharedData[Lag]

object LagSensor:

  /**
   * A static facade for [[LagSensor.senseLag]].
   * @param language
   *   the aggregate foundation that provides the lag sensor
   * @tparam Lag
   *   the type used to represent lag
   * @return
   *   an aggregate value holding the lag from each neighbour (and self)
   */
  def senseLag[Lag](using language: AggregateFoundation & LagSensor[Lag]): language.SharedData[Lag] =
    language.senseLag
