package it.unibo.scafi.utils.boundaries

import it.unibo.scafi.utils.boundaries.Bounded

/**
 * Provides common boundaries for the most common types.
 */
object CommonBoundaries:

  given Bounded[Double] with
    override def lowerBound: Double = Double.NegativeInfinity
    override def upperBound: Double = Double.PositiveInfinity

  given Bounded[Float] with
    override def lowerBound: Float = Float.NegativeInfinity
    override def upperBound: Float = Float.PositiveInfinity
