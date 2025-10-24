package it.unibo.scafi.utils.boundaries

import scala.annotation.nowarn

/**
 * Type class representing a lower bound for a type T.
 * @tparam T
 *   the type of the value
 */
@nowarn("msg=unused implicit parameter")
trait UpperBounded[T: Ordering]:
  /**
   * The upper bound for the type T. Adding a value to this bound should return the same value.
   * @return
   *   the upper bound for the type T
   */
  def upperBound: T
