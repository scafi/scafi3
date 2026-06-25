package it.unibo.scafi.libraries

import scala.concurrent.duration.FiniteDuration
import scala.math.Numeric.Implicits.infixNumericOps

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.CodableFromTo
import it.unibo.scafi.sensors.{ DistanceSensor, LagSensor, TimeSensor }
import it.unibo.scafi.sensors.DistanceSensor.senseDistance
import it.unibo.scafi.utils.boundaries.UpperBounded

import cats.syntax.all.{ catsSyntaxTuple2Semigroupal, catsSyntaxTuple3Semigroupal }

import FieldCalculusLibrary.{ neighborValues, share }
import CommonLibrary.mux

/**
 * This library provides a set of functions to compute the distance between nodes and a source in a network.
 */
object GradientLibrary:

  val DEFAULT_CRF_RAISING_SPEED: Double = 5.0
  val DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON: Double = 0.5
  val DEFAULT_FLEX_DELTA: Double = 0.5

  /**
   * This function computes the distance estimate from a source to this node, based on estimates from the node's
   * neighbours and the distances from the neighbours.
   * @param neighboursEstimates
   *   the estimates from the neighbours
   * @param distances
   *   the distances from the neighbours
   * @tparam N
   *   the type of the distance
   * @return
   *   the distance estimate from a source to a node
   */
  def distanceEstimate[N: {Numeric, UpperBounded}](using
      language: AggregateFoundation,
  )(
      neighboursEstimates: language.SharedData[N],
      distances: language.SharedData[N],
  ): N = (neighboursEstimates, distances).mapN(_ + _).withoutSelf.min

  /**
   * This function computes the distance from a source to this node, by sharing the distance estimate with the
   * neighbours and computing the minimum distance estimate.
   * @param source
   *   whether this node is the source
   * @param distances
   *   the measured distances from the neighbours
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam N
   *   the type of the distance
   * @return
   *   the distance from the source to this node
   */
  def distanceTo[Format, N: {Numeric as numeric, UpperBounded as bound, CodableFromTo[Format]}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(source: Boolean, distances: language.SharedData[N]): N =
    share(bound.upperBound)(av => mux(source)(numeric.zero)(distanceEstimate(av, distances)))

  /**
   * This function computes the distance estimate from a source to this node, based on estimates from the node's
   * neighbours and the distances from the neighbours measured by a distance sensor.
   * @param neighboursEstimates
   *   the estimates from the neighbours
   * @tparam N
   *   the type of the distance
   * @return
   *   the distance estimate from a source to a node
   * @see
   *   [[DistanceSensor.senseDistance]]
   */
  def sensorDistanceEstimate[N: {Numeric, UpperBounded}](using
      language: AggregateFoundation & DistanceSensor[N],
  )(neighboursEstimates: language.SharedData[N]): N =
    distanceEstimate(neighboursEstimates, senseDistance[N])

  /**
   * This function computes the distance from a source to this node, by sharing the distance estimate with the
   * neighbours and computing the minimum distance estimate. The distances are measured by a distance sensor.
   * @param source
   *   whether this node is the source
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam N
   *   the type of the distance
   * @return
   *   the distance from the source to this node
   * @see
   *   [[DistanceSensor.senseDistance]]
   */
  def sensorDistanceTo[Format, N: {Numeric, UpperBounded, CodableFromTo[Format]}](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[N],
  )(source: Boolean): N =
    distanceTo(source, senseDistance[N])

  // ── Time-weighted mean over 1-second windows, used by bisGradient ───────────

  private def meanCounter(value: Double, frequencyMs: Long)(using
      language: AggregateFoundation & FieldCalculusSyntax & TimeSensor,
  ): Double =
    val time = language.timestamp
    val dt = language.deltaTime.toMillis.toDouble
    val count = language.evolve((0.0, 0.0)): (accVal, accTime) =>
      val restart = language
        .evolve((false, time)): (_, lastTime) =>
          (
            Math.floor(time.toDouble / frequencyMs) > Math.floor(lastTime.toDouble / frequencyMs),
            time,
          )
        ._1
      val old = if restart then (0.0, 0.0) else (accVal, accTime)
      if value > Double.NegativeInfinity && value < Double.PositiveInfinity then (old._1 + value * dt, old._2 + dt)
      else old
    if count._2 == 0.0 then 0.0 else count._1 / count._2

  // ── hopGradient ─────────────────────────────────────────────────────────────

  /**
   * Integer hop-count gradient: every edge costs exactly one hop. Equivalent to `distanceTo` with unit metric but
   * returns an `Int` and uses `Int.MaxValue` as the unreachable sentinel.
   *
   * @param source
   *   whether this device is a source
   * @tparam Format
   *   serialisation format for `Int`
   * @return
   *   hop count from the nearest source, or `Int.MaxValue` when unreachable
   */
  def hopGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(
      source: Boolean,
  )(using CodableFromTo[Format][Int], UpperBounded[Int]): Int =
    val inf = summon[UpperBounded[Int]].upperBound
    share[Format, Int](inf): nbrHops =>
      if source then 0
      else
        val minNbr = nbrHops.withoutSelf.min
        if minNbr >= inf then inf else minNbr + 1

  // ── crfGradient ─────────────────────────────────────────────────────────────

  /**
   * Constraint-and-Restoring-Force (CRF) gradient. Estimates rise at `raisingSpeed` when no constraint is satisfied
   * (source removed), and snap down to the true distance when a valid neighbour provides one. Heals faster than the
   * classic gradient on source loss.
   *
   * @param raisingSpeed
   *   upward drift speed when no constraint is active (distance units per second)
   * @param source
   *   whether this device is a source
   * @param metric
   *   per-neighbour distance field
   * @tparam Format
   *   serialisation format for `Double`
   * @return
   *   CRF distance estimate from the nearest source
   * @see
   *   [[GradientLibrary.DEFAULT_CRF_RAISING_SPEED]]
   */
  def crfGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax & TimeSensor & LagSensor[FiniteDuration],
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      raisingSpeed: Double,
      source: Boolean,
      metric: language.SharedData[Double],
  ): Double =
    val inf = summon[UpperBounded[Double]].upperBound
    language
      .evolve((inf, 0.0)): (g, speed) =>
        // Neighbour communication must happen on every device (source included) so the source
        // shares its estimate and the gradient can propagate; only the *returned* value branches.
        val dt = language.deltaTime.toMillis.toDouble / 1000.0
        val nbrGs = neighborValues[Format, Double](g)
        val nbrLags = LagSensor.senseLag[FiniteDuration]
        if source then (0.0, 0.0)
        else
          val constrainedMin = (nbrGs, metric, nbrLags)
            .mapN: (nbrG, d, lag) =>
              val lagSec = lag.toMillis.toDouble / 1000.0
              if nbrG + d + speed * lagSec <= g then nbrG + d else inf
            .withoutSelf
            .min
          if constrainedMin >= inf then (g + raisingSpeed * dt, raisingSpeed)
          else (constrainedMin, 0.0)
      ._1
  end crfGradient

  /**
   * Sensor-based overload of [[crfGradient]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[crfGradient]] for parameter documentation
   */
  def sensorCrfGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax & TimeSensor & LagSensor[FiniteDuration] &
        DistanceSensor[Double],
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      raisingSpeed: Double,
      source: Boolean,
  ): Double = crfGradient[Format](raisingSpeed, source, DistanceSensor.senseDistance[Double])

  // ── bisGradient ─────────────────────────────────────────────────────────────

  /**
   * Bounded-Information-Speed (BIS) gradient. Bounds the speed at which distance estimates can rise, damping the
   * classic rising-value transient when the source moves. The estimate is the tighter of the spatial path length and a
   * speed-limited temporal bound.
   *
   * @param commRadius
   *   communication radius; used in the temporal-bound term to remove the slack from the lag cost
   * @param source
   *   whether this device is a source
   * @param metric
   *   per-neighbour distance field
   * @tparam Format
   *   serialisation format for `Double`
   * @return
   *   BIS distance estimate
   */
  def bisGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax & TimeSensor & LagSensor[FiniteDuration],
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      commRadius: Double,
      source: Boolean,
      metric: language.SharedData[Double],
  ): Double =
    val inf = summon[UpperBounded[Double]].upperBound
    val dt = language.deltaTime.toMillis.toDouble
    val avgFireInterval = meanCounter(dt, 1000L)
    val speed = if avgFireInterval <= 0.0 then 0.0 else DEFAULT_FLEX_DELTA / avgFireInterval
    language
      .evolve((inf, inf)): (spatialDist, tempDist) =>
        // Communicate on every device (source included) so the gradient propagates; branch only the result.
        val nbrSpatials = neighborValues[Format, Double](spatialDist)
        val nbrTemps = neighborValues[Format, Double](tempDist)
        val nbrLags = LagSensor.senseLag[FiniteDuration]
        if source then (0.0, 0.0)
        else
          val spPairs = (nbrSpatials, nbrTemps).mapN((s, t) => (s, t))
          (spPairs, metric, nbrLags)
            .mapN:
              case ((nbrS, nbrT), d, lag) =>
                val newEstimate = Math.max(nbrS + d, speed * nbrT - commRadius)
                val newTemp = nbrT + lag.toMillis.toDouble / 1000.0
                (newEstimate, newTemp)
            .withoutSelf
            .minByOption(_._1)
            .getOrElse((inf, inf))
      ._1
  end bisGradient

  /**
   * Sensor-based overload of [[bisGradient]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[bisGradient]] for parameter documentation
   */
  def sensorBisGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax & TimeSensor & LagSensor[FiniteDuration] &
        DistanceSensor[Double],
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      commRadius: Double,
      source: Boolean,
  ): Double = bisGradient[Format](commRadius, source, DistanceSensor.senseDistance[Double])

  // ── flexGradient ────────────────────────────────────────────────────────────

  /**
   * FLEX gradient: updates the local estimate only when the error exceeds `epsilon`, trading precision for reduced
   * communication cost. Far-away devices carry coarser estimates, saving bandwidth.
   *
   * @param epsilon
   *   tolerance on the slope of the gradient field; smaller → more accurate, higher cost
   * @param delta
   *   minimum fraction of `communicationRadius` treated as neighbour distance (avoids division by zero)
   * @param communicationRadius
   *   nominal communication range
   * @param source
   *   whether this device is a source
   * @param metric
   *   per-neighbour distance field
   * @tparam Format
   *   serialisation format for `Double`
   * @return
   *   FLEX distance estimate; converged value is within `epsilon * distance` of the true distance
   * @see
   *   [[GradientLibrary.DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON]], [[GradientLibrary.DEFAULT_FLEX_DELTA]]
   */
  def flexGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      epsilon: Double,
      delta: Double,
      communicationRadius: Double,
      source: Boolean,
      metric: language.SharedData[Double],
  ): Double =
    val inf = summon[UpperBounded[Double]].upperBound
    language.evolve(inf): g =>
      // Communicate on every device (source included) so the gradient propagates; branch only the result.
      val nbrGs = neighborValues[Format, Double](g)
      if source then 0.0
      else
        // Each neighbour contributes: (nbrG + dist, slope, nbrG, dist)
        // where dist = max(rawMetric, delta * commRadius) guards against zero-division
        val combined = (nbrGs, metric).mapN: (nbrG, d) =>
          val dist = Math.max(d, delta * communicationRadius)
          val slope =
            if dist == 0.0 || nbrG.isInfinite || g.isInfinite then Double.NegativeInfinity
            else (g - nbrG) / dist
          (nbrG + dist, slope, nbrG, dist)
        val constraint = combined.withoutSelf.minByOption(_._1).map(_._1).getOrElse(inf)
        if Math.max(communicationRadius, 2 * constraint) < g then constraint
        else
          combined.withoutSelf.maxByOption(_._2) match
            case None => g
            case Some((_, slope, nbrG, dist)) =>
              if slope > 1 + epsilon then nbrG + (1 + epsilon) * dist
              else if slope < 1 - epsilon then nbrG + (1 - epsilon) * dist
              else g
  end flexGradient

  /**
   * Sensor-based overload of [[flexGradient]]; derives `metric` from [[DistanceSensor.senseDistance]].
   *
   * @see
   *   [[flexGradient]] for parameter documentation
   */
  def sensorFlexGradient[Format](using
      language: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[Double],
  )(using
      CodableFromTo[Format][Double],
      UpperBounded[Double],
  )(
      epsilon: Double,
      delta: Double,
      communicationRadius: Double,
      source: Boolean,
  ): Double = flexGradient[Format](epsilon, delta, communicationRadius, source, DistanceSensor.senseDistance[Double])

end GradientLibrary
