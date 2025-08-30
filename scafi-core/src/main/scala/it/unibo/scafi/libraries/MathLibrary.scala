package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax

import FieldCalculusLibrary.neighborValues
import FoldingLibrary.foldWithoutSelf
import Fractional.Implicits.infixFractionalOps

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
   * @tparam N
   *   the type of the value
   * @return
   *   the weighted average of the value
   */
  def average[N: Fractional](using language: AggregateFoundation & FieldCalculusSyntax)(weight: N, value: N): N =
    val totW = neighborValues(weight).foldWithoutSelf(weight)(_ + _)
    val totV = neighborValues(weight * value).foldWithoutSelf(weight * value)(_ + _)
    totV / totW
end MathLibrary
