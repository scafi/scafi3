package it.unibo.scafi.libraries

import scala.math.Numeric.Implicits.infixNumericOps

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.{ Codable, CodableFromTo }
import it.unibo.scafi.sensors.DistanceSensor
import it.unibo.scafi.utils.boundaries.UpperBounded

import cats.syntax.all.catsSyntaxTuple2Semigroupal

import FieldCalculusLibrary.{ neighborValues, share }
import FieldUtilsLibrary.minHoodSelector
import GradientLibrary.distanceTo

/**
 * Gradient-cast and broadcast operators: the **G** block of aggregate programming. Propagates a value outward from a
 * source along a potential field, optionally transforming it at every hop.
 *
 * @see
 *   [[FieldCalculusLibrary.share]] for the underlying primitive
 * @see
 *   [[FieldUtilsLibrary.minHoodSelector]] for argmin-with-data
 * @see
 *   [[GradientLibrary.distanceTo]] for the gradient potential
 */
object GradientCastLibrary:

  // Single-overload private impl: all public overloads delegate here, avoiding
  // Scala 3 overload-resolution failures on path-dependent SharedData types.
  private def castImpl[Format, V: CodableFromTo[Format], D: {Numeric as num, UpperBounded as bound}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      field: V,
      metric: language.SharedData[D],
      accumulate: V => V,
  ): V =
    val pot = distanceTo[D, D](source, metric)
    val nbrPots = neighborValues[D, D](pot)
    share[Format, V](field): nbrVals =>
      if source then field
      else
        val key = (nbrPots, metric).mapN(_ + _)
        if key.withoutSelf.nonEmpty then accumulate(minHoodSelector(key, nbrVals, field))
        else field

  /**
   * Gradient-cast: propagates `field` outward from `source` along the gradient induced by `metric`, applying
   * `accumulate` at every hop.
   *
   * At the source the value equals `field` unchanged. Elsewhere it equals `accumulate` applied to the value received
   * from the neighbour that minimises `potential + metric`. Isolated non-source nodes return `field`.
   *
   * @param source
   *   whether this device is a source
   * @param field
   *   value held at the source(s)
   * @param metric
   *   per-neighbour distance field (e.g. hop = `neighborValues(1.0)`)
   * @param accumulate
   *   transformation applied at every hop
   * @tparam Format
   *   serialisation format for `V`
   * @tparam V
   *   value type to propagate
   * @tparam D
   *   distance/potential type
   * @return
   *   the propagated value at this device
   * @see
   *   [[broadcast]] for the `accumulate = identity` special case
   */
  def gradientCast[Format, V: CodableFromTo[Format], D: {Numeric as num, UpperBounded as bound}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      field: V,
      metric: language.SharedData[D],
      accumulate: V => V,
  ): V = castImpl(source, field, metric, accumulate)

  /**
   * Sensor-based overload of [[gradientCast]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[gradientCast]] for parameter documentation
   */
  def gradientCast[Format, V: CodableFromTo[Format], D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[D],
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      field: V,
      accumulate: V => V,
  ): V = castImpl(source, field, DistanceSensor.senseDistance[D], accumulate)

  /**
   * Broadcasts `field`'s value from the source(s) outward unchanged (`accumulate = identity`).
   *
   * @param source
   *   whether this device is a source
   * @param field
   *   value to broadcast
   * @param metric
   *   per-neighbour distance field
   * @tparam Format
   *   serialisation format for `V`
   * @tparam V
   *   value type to broadcast
   * @tparam D
   *   distance/potential type
   * @return
   *   `field` at and downstream from the source; `field` default at isolated non-sources
   * @see
   *   [[gradientCast]] for the general form
   */
  def broadcast[Format, V: CodableFromTo[Format], D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      field: V,
      metric: language.SharedData[D],
  ): V = castImpl(source, field, metric, identity)

  /**
   * Sensor-based overload of [[broadcast]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[broadcast]] for parameter documentation
   */
  def broadcast[Format, V: CodableFromTo[Format], D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[D],
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      field: V,
  ): V = castImpl(source, field, DistanceSensor.senseDistance[D], identity)

  /**
   * Distance from `source` to `target` measured along `metric`. Equivalent to broadcasting `distanceTo(target)` from
   * the source.
   *
   * @param source
   *   source node selector
   * @param target
   *   target node selector
   * @param metric
   *   per-neighbour distance field
   * @tparam D
   *   distance type; must be serialisable as itself for in-memory sharing
   * @return
   *   the distance between source and target at every node after convergence
   * @see
   *   [[GradientLibrary.distanceTo]], [[broadcast]]
   */
  def distanceBetween[D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      target: Boolean,
      metric: language.SharedData[D],
  ): D = castImpl[D, D, D](source, distanceTo[D, D](target, metric), metric, identity)

  /**
   * Sensor-based overload of [[distanceBetween]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[distanceBetween]] for parameter documentation
   */
  def distanceBetween[D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[D],
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      target: Boolean,
  ): D =
    val metric = DistanceSensor.senseDistance[D]
    castImpl[D, D, D](source, distanceTo[D, D](target, metric), metric, identity)

  /**
   * Boolean channel of width `width` connecting `source` to `target`: `true` on devices that lie close to a minimal
   * source→target path.
   *
   * A device is in the channel when `dist(source) + dist(target) ≤ distanceBetween(source, target) + width`, guarding
   * against unreachable nodes (either distance equals `upperBound`).
   *
   * @param source
   *   source node selector
   * @param target
   *   target node selector
   * @param metric
   *   per-neighbour distance field
   * @param width
   *   allowed deviation from the optimal path
   * @tparam D
   *   distance type
   * @return
   *   `true` iff this device is within `width` of the shortest path
   * @see
   *   [[distanceBetween]]
   */
  def channel[D: {Numeric as num, UpperBounded as bound}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      target: Boolean,
      metric: language.SharedData[D],
      width: D,
  ): Boolean =
    val ord = summon[Ordering[D]]
    val inf = bound.upperBound
    val distSource = distanceTo[D, D](source, metric)
    val distTarget = distanceTo[D, D](target, metric)
    val db = castImpl[D, D, D](source, distanceTo[D, D](target, metric), metric, identity)
    ord.lt(distSource, inf) && ord.lt(distTarget, inf) &&
    ord.lteq(distSource + distTarget, db + width)

  /**
   * Sensor-based overload of [[channel]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[channel]] for parameter documentation
   */
  def channel[D: {Numeric as num, UpperBounded as bound}](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[D],
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
  )(
      source: Boolean,
      target: Boolean,
      width: D,
  ): Boolean =
    val metric = DistanceSensor.senseDistance[D]
    val ord = summon[Ordering[D]]
    val inf = bound.upperBound
    val distSource = distanceTo[D, D](source, metric)
    val distTarget = distanceTo[D, D](target, metric)
    val db = castImpl[D, D, D](source, distanceTo[D, D](target, metric), metric, identity)
    ord.lt(distSource, inf) && ord.lt(distTarget, inf) &&
    ord.lteq(distSource + distTarget, db + width)

end GradientCastLibrary
