package it.unibo.scafi.utils.boundaries

/**
 * Type class representing a lower bound for a type T.
 * @tparam T
 *   the type of the value
 */
trait UpperBounded[T: Ordering]:
  /**
   * The upper bound for the type T. Adding a value to this bound should return the same value.
   * @return
   *   the upper bound for the type T
   */
  def upperBound: T
