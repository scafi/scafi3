package it.unibo.scafi.abstractions

import scala.annotation.targetName

/**
 * A bidirectional function is a function that can be applied in two directions. It is useful to define a function and
 * its inverse in a single object.
 * @tparam A
 *   the input type
 * @tparam B
 *   the output type
 * @param forward
 *   the forward function
 * @param backward
 *   the backward function
 */
case class BidirectionalFunction[A, B](forward: A => B, backward: B => A)

object BidirectionalFunction:

  /**
   * A bidirectional function type, defined using an infix type operator.
   */
  @targetName("bidirectionalFunctionType")
  infix type <=>[A, B] = BidirectionalFunction[A, B]

  /**
   * Creates a bidirectional function that is the identity function.
   * @tparam A
   *   the input and output type
   * @return
   *   a bidirectional function that is the identity function
   */
  @targetName("bidirectionalFunction0")
  def <=>[A]: A <=> A = BidirectionalFunction[A, A](x => x, x => x)

  /**
   * Creates a bidirectional function from a forward function. The backward function is the identity function.
   * @tparam A
   *   the input and output type
   * @param forward
   *   the forward function
   * @return
   *   a bidirectional function
   */
  @targetName("bidirectionalFunction1")
  def <=>[A](forward: A => A): A <=> A =
    BidirectionalFunction[A, A](forward, x => x)

  /**
   * Creates a bidirectional function from a forward and a backward function.
   * @tparam A
   *   the input type
   * @tparam B
   *   the output type
   * @param forward
   *   the forward function
   * @param backward
   *   the backward function
   * @return
   *   a bidirectional function
   */
  @targetName("bidirectionalFunction2")
  infix def <=>[A, B](forward: A => B, backward: B => A): A <=> B =
    BidirectionalFunction[A, B](forward, backward)
end BidirectionalFunction
