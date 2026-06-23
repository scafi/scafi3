package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.{ Codable, CodableFromTo }
import it.unibo.scafi.utils.boundaries.UpperBounded

import cats.syntax.all.catsSyntaxTuple2Semigroupal

import FieldCalculusLibrary.{ neighborValues, share }
import CommonLibrary.mux

/**
 * Collection (the **C** block): the dual of gradient-cast. Accumulates values from the whole network *down* a potential
 * field toward a sink, summing/merging along the spanning tree induced by the potential.
 *
 * Each device selects, as its **parent**, the neighbour with the strictly smallest potential, and folds its own `local`
 * value with the contributions of all neighbours for which it is the parent. `share` makes the partial sums flow
 * downhill, so the sink ends up holding the aggregate over its whole reachable region.
 *
 * @see
 *   [[FieldCalculusLibrary.share]] for the underlying primitive
 * @see
 *   [[GradientLibrary.distanceTo]] for a typical potential
 * @see
 *   [[GradientCastLibrary]] for the dual (broadcast) operator
 */
object CollectionLibrary:

  /**
   * The id of this device's parent along `potential`: the neighbour with the strictly smallest potential (ties broken
   * by the smaller device id). If no neighbour has a strictly smaller potential the device is its own root, and the
   * upper-bound sentinel of [[AggregateFoundation.DeviceId]] is returned.
   *
   * @param potential
   *   the per-neighbour potential field (lower = closer to the sink)
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @return
   *   the parent device id, or the `DeviceId` upper bound when the device is a local minimum
   * @see
   *   [[findParentOpt]] for the total, sentinel-free variant
   */
  def findParent[P: Ordering](using
      language: AggregateFoundation,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
  )(
      potential: language.SharedData[P],
  ): language.DeviceId =
    findParentOpt(potential).getOrElse(summon[UpperBounded[language.DeviceId]].upperBound)

  /**
   * The id of this device's parent along `potential`, or `None` when the device is a local minimum (its own root).
   *
   * @param potential
   *   the per-neighbour potential field (lower = closer to the sink)
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @return
   *   `Some(parentId)` for the strictly-lower-potential neighbour, or `None`
   * @see
   *   [[findParent]] for the sentinel variant
   */
  def findParentOpt[P: Ordering as ordP](using
      language: AggregateFoundation,
  )(using
      Ordering[language.DeviceId],
  )(
      potential: language.SharedData[P],
  ): Option[language.DeviceId] =
    val myPotential = potential.onlySelf
    (potential, language.device)
      .mapN((p, id) => (p, id))
      .withoutSelf
      .minOption
      .filter((minP, _) => ordP.lt(minP, myPotential))
      .map(_._2)

  /**
   * Collects `local` values down `potential` toward the sink, combining contributions with `accumulate`. A device sums
   * its own `local` with the collected values of every neighbour that selected it as parent; `zero` is the neutral
   * contribution for non-children.
   *
   * @param potential
   *   the per-neighbour potential field; lower = closer to the sink
   * @param local
   *   the value contributed by this device
   * @param accumulate
   *   associative, commutative combination of contributions (with `zero` as identity)
   * @param zero
   *   the neutral contribution for neighbours that did not select this device as parent
   * @tparam Format
   *   serialisation format for the collected value `V`
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam V
   *   the collected value type
   * @return
   *   the value collected at this device (the full aggregate at the sink)
   * @see
   *   [[findParent]], [[FieldUtilsLibrary.minHoodSelector]]
   */
  def collect[Format, P: Ordering, V: CodableFromTo[Format]](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
  )(
      potential: language.SharedData[P],
      local: V,
      accumulate: (V, V) => V,
      zero: V,
  ): V =
    // Each device shares which neighbour it picked as parent; a child's value flows to that parent.
    val parents = neighborValues[language.DeviceId, language.DeviceId](findParent(potential))
    share[Format, V](local): collected =>
      val contributions = (parents, collected).mapN: (parentId, nbrValue) =>
        mux(summon[Ordering[language.DeviceId]].equiv(parentId, language.localId))(nbrValue)(zero)
      // Fold neighbours only (`withoutSelf`): a device is never its own parent, so self contributes `zero`;
      // we also avoid the full-field iterator, which reflects the live alignment scope rather than this field.
      accumulate(local, contributions.withoutSelf.foldLeft(zero)(accumulate))

  /**
   * Counts the devices (optionally satisfying `predicate`) collected to the sink.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param predicate
   *   when `true`, this device contributes `1` to the count
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @return
   *   the number of reachable devices satisfying `predicate`, accumulated at the sink
   */
  def collectCount[P: Ordering](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Long, Long],
  )(
      potential: language.SharedData[P],
      predicate: Boolean,
  ): Long =
    collect[Long, P, Long](potential, mux(predicate)(1L)(0L), _ + _, 0L)

  /**
   * Mean of `value` over the collected region, available at the sink. Total: an isolated node reports its own `value`.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param value
   *   the per-device value to average
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @return
   *   the network-region mean of `value` accumulated at the sink
   */
  def collectMean[P: Ordering](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Long, Long],
      Codable[Double, Double],
  )(
      potential: language.SharedData[P],
      value: Double,
  ): Double =
    val count = collectCount(potential, predicate = true)
    val total = collect[Double, P, Double](potential, value, _ + _, 0.0)
    if count == 0L then value else total / count.toDouble

  /**
   * Collects map-valued contributions down to the sink, resolving key collisions with `merge`.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param local
   *   the map contributed by this device
   * @param merge
   *   collision policy applied to two values sharing a key: `merge(key, accumulated, incoming)`
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam K
   *   the map key type
   * @tparam V
   *   the map value type
   * @return
   *   the merged map accumulated at the sink
   * @see
   *   [[FieldUtilsLibrary.mergeHood]]
   */
  def collectMaps[P: Ordering, K, V](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Map[K, V], Map[K, V]],
  )(
      potential: language.SharedData[P],
      local: Map[K, V],
      merge: (K, V, V) => V,
  ): Map[K, V] =
    collect[Map[K, V], P, Map[K, V]](potential, local, mergeMaps(merge), Map.empty)

  /**
   * [[collectMaps]] with the default "keep first" collision policy.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param local
   *   the map contributed by this device
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam K
   *   the map key type
   * @tparam V
   *   the map value type
   * @return
   *   the merged map accumulated at the sink, keeping the first value seen per key
   */
  def collectMaps[P: Ordering, K, V](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Map[K, V], Map[K, V]],
  )(
      potential: language.SharedData[P],
      local: Map[K, V],
  ): Map[K, V] =
    collect[Map[K, V], P, Map[K, V]](potential, local, mergeMaps((_: K, accumulated: V, _: V) => accumulated), Map.empty)

  /**
   * Collects the union of all `local` sets at the sink.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param local
   *   the set contributed by this device
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam T
   *   the set element type
   * @return
   *   the union of all reachable devices' sets, accumulated at the sink
   */
  def collectSets[P: Ordering, T](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Set[T], Set[T]],
  )(
      potential: language.SharedData[P],
      local: Set[T],
  ): Set[T] =
    collect[Set[T], P, Set[T]](potential, local, _ union _, Set.empty)

  /**
   * Collects each device's single `local` value into a set at the sink.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param local
   *   the value contributed by this device
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam T
   *   the value type
   * @return
   *   the set of all reachable devices' values, accumulated at the sink
   * @see
   *   [[collectSets]]
   */
  def collectIntoSet[P: Ordering, T](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Set[T], Set[T]],
  )(
      potential: language.SharedData[P],
      local: T,
  ): Set[T] =
    collectSets(potential, Set(local))

  /**
   * Collects values into a map keyed by their originating device id.
   *
   * @param potential
   *   the per-neighbour potential field
   * @param local
   *   the value contributed by this device
   * @tparam P
   *   the potential type; must have an [[Ordering]]
   * @tparam T
   *   the value type
   * @return
   *   the map `deviceId -> value` over all reachable devices, accumulated at the sink
   * @see
   *   [[collectSets]]
   */
  def collectValuesByDevices[P: Ordering, T](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Ordering[language.DeviceId],
      UpperBounded[language.DeviceId],
      Codable[language.DeviceId, language.DeviceId],
      Codable[Set[(language.DeviceId, T)], Set[(language.DeviceId, T)]],
  )(
      potential: language.SharedData[P],
      local: T,
  ): Map[language.DeviceId, T] =
    collectSets(potential, Set(language.localId -> local)).toMap

  // Pairwise, total merge of two maps honouring the per-key `merge` collision policy.
  private def mergeMaps[K, V](merge: (K, V, V) => V)(left: Map[K, V], right: Map[K, V]): Map[K, V] =
    right.foldLeft(left) { case (accumulated, (key, value)) =>
      accumulated.updatedWith(key):
        case Some(existing) => Some(merge(key, existing, value))
        case None => Some(value)
    }
end CollectionLibrary
