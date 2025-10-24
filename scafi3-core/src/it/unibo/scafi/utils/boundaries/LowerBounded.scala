package it.unibo.scafi.utils.boundaries

import scala.annotation.nowarn

/**
 * Type class representing a bounded type.
 * @tparam T
 *   the type of the value
 */
@nowarn("msg=unused implicit parameter")
trait LowerBounded[T: Ordering]:
  /**
   * The lower bound of the type. Subtracting a value from this bound should return the same value.
   * @return
   *   the lower bound
   */
  def lowerBound: T
