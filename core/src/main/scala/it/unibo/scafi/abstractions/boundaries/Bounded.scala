package it.unibo.scafi.abstractions.boundaries

/**
 * Type class representing upper and lower bounds on a type T.
 * @tparam T
 *   the type of the values
 */
trait Bounded[T: Ordering] extends LowerBounded[T], UpperBounded[T]
