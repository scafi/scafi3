package it.unibo.scafi.utils.boundaries

import scala.annotation.nowarn

/**
 * Type class representing upper and lower bounds on a type T.
 * @tparam T
 *   the type of the values
 */
@nowarn("msg=unused implicit parameter")
trait Bounded[T: Ordering] extends LowerBounded[T], UpperBounded[T]
