package it.unibo.scafi.sensors

import it.unibo.scafi.language.AggregateFoundation

/**
 * If an aggregate foundation implements this trait, it provides a stream of pseudo-random numbers, one draw per round.
 */
trait RandomGenerator:
  this: AggregateFoundation =>

  /**
   * @return
   *   a fresh pseudo-random number, uniformly distributed in `[0, 1)`.
   */
  def nextRandom: Double

object RandomGenerator:

  /**
   * A static facade for [[RandomGenerator.nextRandom]].
   * @param language
   *   the aggregate foundation that provides the random generator
   * @return
   *   a fresh pseudo-random number, uniformly distributed in `[0, 1)`
   */
  def nextRandom(using language: AggregateFoundation & RandomGenerator): Double = language.nextRandom
