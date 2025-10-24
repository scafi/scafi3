package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.libraries.FieldCalculusLibrary.neighborValues
import it.unibo.scafi.libraries.FoldingLibrary.foldWithoutSelf
import it.unibo.scafi.message.CodableFromTo

import scala.math.Fractional.Implicits.infixFractionalOps

/**
 * This library provides a set of mathematical functions.
 */
object MathLibrary:

  /**
   * Given a weight and a value, computes the weighted average of a value across the immediate neighbours of the current
   * device.
   * @param weight
   *   the weight of the value for this device
   * @param value
   *   the value to be averaged
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam N
   *   the type of the value
   * @return
   *   the weighted average of the value
   */
  def average[Format, N: {Fractional, CodableFromTo[Format]}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(weight: N, value: N): N =
    val totW = neighborValues(weight).foldWithoutSelf(weight)(_ + _)
    val totV = neighborValues(weight * value).foldWithoutSelf(weight * value)(_ + _)
    totV / totW
end MathLibrary
