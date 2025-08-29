package it.unibo.scafi.sensors

import scala.annotation.nowarn

import it.unibo.scafi.language.AggregateFoundation

/**
 * If an aggregate foundation implements this trait, it provides a way to measure the distance from the neighbours and
 * encapsulate the result in an aggregate value.
 * @tparam Distance
 *   the type of the distance measure
 */
@nowarn("msg=unused implicit parameter")
trait DistanceSensor[Distance: Numeric]:
  this: AggregateFoundation =>

  /**
   * Measures the distance from the neighbours and encapsulates the result in an aggregate value.
   * @return
   *   the aggregate value that encapsulates the distance measure
   */
  def senseDistance: SharedData[Distance]

object DistanceSensor:

  /**
   * A static facade for the [[DistanceSensor.senseDistance]] method. Measures the distance from the neighbours and
   * encapsulates the result in an aggregate value.
   * @param language
   *   the aggregate foundation that provides the distance measure
   * @tparam Distance
   *   the type of the distance measure
   * @return
   *   the aggregate value that encapsulates the distance measure
   */
  @nowarn("msg=unused implicit parameter")
  def senseDistance[Distance: Numeric](using
      language: AggregateFoundation & DistanceSensor[Distance],
  ): language.SharedData[Distance] =
    language.senseDistance
