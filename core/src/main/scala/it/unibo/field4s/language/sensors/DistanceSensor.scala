package it.unibo.field4s.language.sensors

import it.unibo.field4s.language.foundation.AggregateFoundation

/**
 * If an aggregate foundation implements this trait, it provides a way to measure the distance from the neighbours and
 * encapsulate the result in an aggregate value.
 * @tparam N
 *   the type of the distance measure
 */
trait DistanceSensor[N: Numeric]:
  this: AggregateFoundation =>

  /**
   * Measures the distance from the neighbours and encapsulates the result in an aggregate value.
   * @return
   *   the aggregate value that encapsulates the distance measure
   */
  def senseDistance: AggregateValue[N]

object DistanceSensor:

  /**
   * A static facade for the [[DistanceSensor.senseDistance]] method. Measures the distance from the neighbours and
   * encapsulates the result in an aggregate value.
   * @param language
   *   the aggregate foundation that provides the distance measure
   * @tparam N
   *   the type of the distance measure
   * @return
   *   the aggregate value that encapsulates the distance measure
   */
  def senseDistance[N: Numeric](using language: AggregateFoundation & DistanceSensor[N]): language.AggregateValue[N] =
    language.senseDistance
