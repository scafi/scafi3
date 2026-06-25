package it.unibo.scafi.libraries

import scala.math.Numeric.Implicits.infixNumericOps

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.{ Codable, CodableFromTo }
import it.unibo.scafi.sensors.RandomGenerator
import it.unibo.scafi.sensors.RandomGenerator.nextRandom
import it.unibo.scafi.utils.boundaries.UpperBounded

import cats.syntax.all.catsSyntaxTuple4Semigroupal

import FieldCalculusLibrary.{ evolve, neighborValues, share }
import GradientLibrary.distanceTo

/**
 * Sparse-choice / leader election (the **S** block). Elects a sparse set of leaders such that any two leaders are
 * roughly `grain` apart — the basis for partitioning a network into regions, multi-leader coordination, and so on.
 *
 * Every device starts as a candidate leader carrying a unique id `uid = (randomSeed, deviceId)`. Devices then
 * **compete**: a device abdicates in favour of a nearby device with a lower UID, where "nearby" is decided by a gradient
 * distance measured against `grain`. The surviving leaders form a Poisson-disc-like sparse set with mean spacing
 * `grain`.
 *
 * '''Design choices for the abstract [[AggregateFoundation.DeviceId]]''':
 *   - UIDs are ordered '''lexicographically''': by the random seed first, then by device id as a deterministic
 *     tie-break. This requires only an `Ordering[DeviceId]`.
 *   - [[minId]] gossips the minimum id over a connected component and therefore needs an
 *     [[UpperBounded]] `DeviceId` for the empty-neighbourhood case.
 *
 * @see
 *   [[GradientLibrary.distanceTo]] for the distance gradient driving the competition
 * @see
 *   [[GradientCastLibrary]] for the dual broadcast operator
 * @see
 *   [[RandomGenerator]] for the per-round random stream backing [[randomUid]]
 */
object SparseChoiceLibrary:

  /**
   * Elects a sparse set of leaders that are roughly `grain` apart, measuring distance with `metric`.
   *
   * @param grain
   *   the mean target spacing between two leaders
   * @param metric
   *   the per-neighbour distance field (e.g. hop = `neighborValues(1)`)
   * @tparam Format
   *   serialisation format for the shared UID
   * @tparam D
   *   the distance/grain type
   * @return
   *   `true` iff this device is a surviving leader
   * @see
   *   [[breakUsingUids]] for the underlying competition
   */
  def sparseChoice[Format, D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax & RandomGenerator,
  )(using
      Ordering[language.DeviceId],
      Codable[D, D],
      CodableFromTo[Format][(Double, language.DeviceId)],
  )(
      grain: D,
      metric: language.SharedData[D],
  ): Boolean =
    breakUsingUids[Format, D](randomUid, grain, metric)

  /**
   * The minimum device id over this device's connected component (a gossip of the component-wide minimum).
   *
   * @tparam Format
   *   serialisation format for the shared id
   * @return
   *   the smallest reachable device id; the device's own id on an isolated node
   */
  def minId[Format](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      ordering: Ordering[language.DeviceId],
      bound: UpperBounded[language.DeviceId],
      codec: CodableFromTo[Format][language.DeviceId],
  ): language.DeviceId =
    share[Format, language.DeviceId](bound.upperBound): neighbouringIds =>
      ordering.min(language.localId, neighbouringIds.withoutSelf.min)

  /**
   * A stable, per-device unique id `(randomSeed, deviceId)`. The random seed is '''latched''' on the first round (via
   * [[FieldCalculusLibrary.evolve]]) so the UID never changes across rounds, while the device id guarantees global
   * uniqueness even on a seed collision.
   *
   * @return
   *   this device's stable unique id
   */
  def randomUid(using
      language: AggregateFoundation & FieldCalculusSyntax & RandomGenerator,
  ): (Double, language.DeviceId) =
    evolve((nextRandom, language.localId))(latched => (latched._1, language.localId))

  /**
   * Symmetry-breaking competition driving [[sparseChoice]], exposed because it is a reusable building block: it elects
   * leaders given an arbitrary, externally-provided `uid` field rather than [[randomUid]].
   *
   * Each device shares the UID of the leader it currently follows. It measures the gradient distance to that leader and,
   * via [[distanceCompetition]], either keeps following it, abdicates, or re-stands as a candidate for a new region.
   *
   * @param uid
   *   this device's unique id (lower wins ties)
   * @param grain
   *   the mean target spacing between two leaders
   * @param metric
   *   the per-neighbour distance field
   * @tparam Format
   *   serialisation format for the shared UID
   * @tparam D
   *   the distance/grain type
   * @return
   *   `true` iff this device survives as a leader (it still follows its own `uid`)
   */
  def breakUsingUids[Format, D: {Numeric, UpperBounded}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      ordering: Ordering[language.DeviceId],
      distanceCodec: Codable[D, D],
      codec: CodableFromTo[Format][(Double, language.DeviceId)],
  )(
      uid: (Double, language.DeviceId),
      grain: D,
      metric: language.SharedData[D],
  ): Boolean =
    val elected = share[Format, (Double, language.DeviceId)](uid): leadField =>
      val lead = leadField.onlySelf
      // Distance from this device to the leader it currently follows (0 when it is the leader).
      val distanceToLeader = distanceTo[D, D](sameUid(lead, uid), metric)
      distanceCompetition[D](distanceToLeader, leadField, uid, grain, metric)
    sameUid(elected, uid)

  /**
   * Candidate leaders surrender leadership to the lowest nearby UID. Given the gradient `distanceToLeader` to the
   * currently-followed leader, a device:
   *   - re-stands as a candidate for its own region when farther than `grain` from the leader;
   *   - abdicates (yields `+∞`) at an intermediate distance (`distanceToLeader ≥ grain / 2`);
   *   - otherwise follows the lowest UID among nearby leaders.
   *
   * Comparisons against `grain / 2` are expressed by '''doubling''' the distance (`2·d ≥ grain`) so the whole block
   * needs only a [[Numeric]] `D`, never a `Fractional` one.
   *
   * @param distanceToLeader
   *   gradient distance from this device to the leader it currently follows
   * @param leadField
   *   the shared field of neighbours' currently-followed leaders
   * @param uid
   *   this device's unique id
   * @param grain
   *   the mean target spacing between two leaders
   * @param metric
   *   the per-neighbour distance field
   * @return
   *   the leader UID this device follows after the competition step
   */
  private def distanceCompetition[D: {Numeric as num}](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      ordering: Ordering[language.DeviceId],
      distanceCodec: Codable[D, D],
  )(
      distanceToLeader: D,
      leadField: language.SharedData[(Double, language.DeviceId)],
      uid: (Double, language.DeviceId),
      grain: D,
      metric: language.SharedData[D],
  ): (Double, language.DeviceId) =
    given uidOrdering: Ordering[(Double, language.DeviceId)] = uidLexicographicOrdering(using ordering)
    def twice(value: D): D = value + value
    // Hoisted above the branch so neighbour communication happens on every device, every round (alignment).
    val neighbourDistances = neighborValues[D, D](distanceToLeader)
    if num.gt(distanceToLeader, grain) then uid
    else if num.gteq(twice(distanceToLeader), grain) then (Double.PositiveInfinity, uid._2)
    else
      // Among nearby leaders, abdicating ones (too far via this neighbour) contribute +∞ tagged with their own id;
      // the lexicographic min then selects the lowest surviving UID, self included.
      val nearbyLeaders = (neighbourDistances, metric, leadField, language.device)
        .mapN: (neighbourDistance, edge, neighbourLead, neighbourId) =>
          if num.gteq(twice(neighbourDistance + edge), grain) then (Double.PositiveInfinity, neighbourId)
          else neighbourLead
        .withoutSelf
      (nearbyLeaders.toList :+ leadField.onlySelf).min(using uidOrdering)

  /** Lexicographic ordering on UIDs: by random seed first, then by device id as a deterministic tie-break. */
  private def uidLexicographicOrdering[Id](using idOrdering: Ordering[Id]): Ordering[(Double, Id)] =
    (left, right) =>
      val bySeed = java.lang.Double.compare(left._1, right._1)
      if bySeed != 0 then bySeed else idOrdering.compare(left._2, right._2)

  /** UID equality consistent with [[uidLexicographicOrdering]], avoiding multiversal-equality on the abstract id. */
  private def sameUid[Id](left: (Double, Id), right: (Double, Id))(using idOrdering: Ordering[Id]): Boolean =
    left._1 == right._1 && idOrdering.equiv(left._2, right._2)
end SparseChoiceLibrary
