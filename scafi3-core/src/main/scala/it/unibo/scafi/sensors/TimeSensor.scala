package it.unibo.scafi.sensors

import scala.concurrent.duration.FiniteDuration

import it.unibo.scafi.language.AggregateFoundation

/**
 * If an aggregate foundation implements this trait, it provides the local notion of time: the wall-clock instant and
 * the time elapsed since the previous round.
 */
trait TimeSensor:
  this: AggregateFoundation =>

  /**
   * @return
   *   the time elapsed between the previous round and the current one.
   */
  def deltaTime: FiniteDuration

  /**
   * @return
   *   the wall-clock instant at which the current round is evaluated.
   */
  def timestamp: Long

object TimeSensor:

  /**
   * A static facade for [[TimeSensor.deltaTime]].
   * @param language
   *   the aggregate foundation that provides the time sensor
   * @return
   *   the time elapsed between the previous round and the current one
   */
  def deltaTime(using language: AggregateFoundation & TimeSensor): FiniteDuration = language.deltaTime

  /**
   * A static facade for [[TimeSensor.timestamp]].
   * @param language
   *   the aggregate foundation that provides the time sensor
   * @return
   *   the wall-clock instant at which the current round is evaluated
   */
  def timestamp(using language: AggregateFoundation & TimeSensor): Long = language.timestamp
