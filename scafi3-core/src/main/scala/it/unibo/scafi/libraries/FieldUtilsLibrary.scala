package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation

import cats.syntax.all.catsSyntaxTuple3Semigroupal

/**
 * Field reduction utilities providing argmin/argmax selectors and map-merging over neighbour fields.
 *
 * Several legacy `FieldUtils` operations are already expressible with the existing APIs:
 * {{{
 * // sumHood(e)            → neighborValues(e).fold(zero)(_ + _)
 * // anyHood(e)            → neighborValues(e).fold(false)(_ || _)
 * // everyHood(e)          → neighborValues(e).fold(true)(_ && _)
 * // minHoodLoc(d)(e)      → neighborValues(e).withoutSelf.fold(d)(_.min(_))
 * // includingSelf/excludingSelf → pass `field` vs `field.withoutSelf`
 * // reifyField(e)         → (device, neighborValues(e)).mapN(_ -> _).toList.toMap
 * }}}
 *
 * This object adds only the genuinely-missing primitives: argmin/argmax selectors and map merging.
 */
object FieldUtilsLibrary:

  /**
   * Returns the `data` value associated with the neighbour minimising `key`, excluding the local device. Total:
   * returns `default` for an empty neighbourhood. Ties broken by the smaller device identifier.
   *
   * To include the local device in the comparison, pass `key` and `data` with self already present and use the
   * `includingSelf` variant by calling this method on the unfiltered `SharedData`.
   *
   * @param key
   *   the shared field of keys used to rank neighbours
   * @param data
   *   the shared field of data values to select from
   * @param default
   *   the value returned when no neighbours are present after excluding self
   * @tparam K
   *   the key type; must have an [[Ordering]]
   * @tparam V
   *   the data type to return
   * @return
   *   the data value of the neighbour with the minimum key, or `default`
   * @see [[maxHoodSelector]] for the dual operation
   */
  def minHoodSelector[K: Ordering, V](using language: AggregateFoundation)(using Ordering[language.DeviceId])(
      key: language.SharedData[K],
      data: language.SharedData[V],
      default: V,
  ): V =
    (key, data, language.device)
      .mapN { (k, v, id) => (k, id, v) }
      .withoutSelf
      .minByOption { (k, id, _) => (k, id) }
      .map(_._3)
      .getOrElse(default)

  /**
   * Returns the `data` value associated with the neighbour maximising `key`, excluding the local device. Total:
   * returns `default` for an empty neighbourhood. Ties broken by the larger device identifier.
   *
   * @param key
   *   the shared field of keys used to rank neighbours
   * @param data
   *   the shared field of data values to select from
   * @param default
   *   the value returned when no neighbours are present after excluding self
   * @tparam K
   *   the key type; must have an [[Ordering]]
   * @tparam V
   *   the data type to return
   * @return
   *   the data value of the neighbour with the maximum key, or `default`
   * @see [[minHoodSelector]] for the dual operation
   */
  def maxHoodSelector[K: Ordering, V](using language: AggregateFoundation)(using Ordering[language.DeviceId])(
      key: language.SharedData[K],
      data: language.SharedData[V],
      default: V,
  ): V =
    (key, data, language.device)
      .mapN { (k, v, id) => (k, id, v) }
      .withoutSelf
      .maxByOption { (k, id, _) => (k, id) }
      .map(_._3)
      .getOrElse(default)

  /**
   * Key-wise merge of map-valued neighbour contributions. On key collision the `overwrite` policy decides the
   * surviving value: `overwrite(existing, incoming)`.
   *
   * @param maps
   *   the shared field of maps to merge (may include self)
   * @param overwrite
   *   collision policy: first argument is the value accumulated so far, second is the incoming value
   * @tparam K
   *   the map key type
   * @tparam V
   *   the map value type
   * @return
   *   a single [[Map]] with all keys from all neighbours, resolved via `overwrite`
   * @see [[FoldingLibrary]] for simple `fold`/`foldWithoutSelf` patterns
   */
  def mergeHood[K, V](using language: AggregateFoundation)(
      maps: language.SharedData[Map[K, V]],
  )(overwrite: (V, V) => V): Map[K, V] =
    maps.foldLeft(Map.empty[K, V]) { (acc, m) =>
      m.foldLeft(acc) { case (a, (k, v)) =>
        a.updatedWith(k):
          case None            => Some(v)
          case Some(existing) => Some(overwrite(existing, v))
      }
    }
end FieldUtilsLibrary
