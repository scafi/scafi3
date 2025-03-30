package it.unibo.scafi.collections

import scala.annotation.targetName
import scala.annotation.unchecked.uncheckedVariance
import scala.collection.{ Iterable, MapView }

/**
 * A map with a default value for missing keys. Implements [[Function1]][K, V] and [[Iterable]][(K, V)].
 * @param underlying
 *   the underlying map that implements [[PartialFunction]][K, V]
 * @param default
 *   the default value for missing keys
 * @tparam K
 *   the key type
 * @tparam V
 *   the value type
 */
class MapWithDefault[K, +V](underlying: Map[K, V], val default: V) extends Iterable[(K, V)] with Function[K, V]
    derives CanEqual:

  private val inner: Map[K, V] = underlying.withDefaultValue(default)

  override def apply(v1: K): V = inner(v1)

  override def iterator: Iterator[(K, V)] = inner.iterator

  override def toString(): String =
    mkString(s"$className<$default>(", ", ", ")")

  override def className: String = "MapWithDefault"

  override def equals(obj: Any): Boolean = obj match
    case that: MapWithDefault[?, ?] =>
      this.inner.equals(that.inner) && this.default.equals(that.default)
    case _ => false

  override def hashCode(): Int = 31 * underlying.## + default.##

  @targetName("addElements")
  inline def ++[V1 >: V](xs: IterableOnce[(K, V1)]): MapWithDefault[K, V1] =
    new MapWithDefault(inner ++ xs, default)

  @targetName("addElement")
  inline def +[V1 >: V](kv: (K, V1)): MapWithDefault[K, V1] =
    new MapWithDefault(inner + kv, default)

  @targetName("removeElement")
  inline def -(key: K): MapWithDefault[K, V] =
    new MapWithDefault(inner - key, default)

  @targetName("removeElements")
  inline def --(keys: IterableOnce[K]): MapWithDefault[K, V] =
    new MapWithDefault(inner -- keys, default)

  def concat[V1 >: V](other: IterableOnce[(K, V1)]): MapWithDefault[K, V1] =
    new MapWithDefault(inner.concat(other), default)

  override def empty: MapWithDefault[K, V] =
    new MapWithDefault(Map.empty, default)

  override def filter(p: ((K, V)) => Boolean): MapWithDefault[K, V] =
    new MapWithDefault(inner.filter(p), default)

  override def filterNot(p: ((K, V)) => Boolean): MapWithDefault[K, V] =
    new MapWithDefault(inner.filterNot(p), default)

  def flatMap[K2 <: K](
      f: ((K, V)) => IterableOnce[(K2, V @uncheckedVariance)],
  ): MapWithDefault[K2, V] =
    new MapWithDefault(inner.flatMap(f), default)

  def get(key: K): V = inner(key)

  override def knownSize: Int = inner.knownSize

  def mapValues[V2](f: V => V2): MapWithDefault[K, V2] =
    new MapWithDefault(inner.map((k, v) => (k, f(v))), f(default))

  def mapKeys[K2](f: K => K2): MapWithDefault[K2, V] =
    new MapWithDefault(inner.map((k, v) => (f(k), v)), default)

  override def partition(
      p: ((K, V)) => Boolean,
  ): (MapWithDefault[K, V], MapWithDefault[K, V]) =
    val (l, r) = inner.partition(p)
    (new MapWithDefault(l, default), new MapWithDefault(r, default))

  def removed(key: K): MapWithDefault[K, V] =
    new MapWithDefault(inner.removed(key), default)

  def removedAll(keys: IterableOnce[K]): MapWithDefault[K, V] =
    new MapWithDefault(inner.removedAll(keys), default)

  override def size: Int = inner.size

  override def tapEach[U](f: ((K, V)) => U): MapWithDefault[K, V] =
    val _ = inner.tapEach(f)
    this

  def updated[V1 >: V](key: K, value: V1): MapWithDefault[K, V1] =
    new MapWithDefault(inner.updated(key, value), default)

  def updatedWith[V1 >: V](key: K)(f: V1 => Option[V1]): MapWithDefault[K, V1] =
    new MapWithDefault(
      inner.updatedWith(key)(v => f(v.getOrElse(default))),
      default,
    )

  def withDefault[V1 >: V](d: V1): MapWithDefault[K, V1] =
    new MapWithDefault(inner, d)

  override def view: MapView[K, V] = inner.view

  override def toMap[K2, V2](implicit ev: (K, V) <:< (K2, V2)): Map[K2, V2] =
    inner.toMap

  export inner.{
    contains,
    flatMap,
    foreachEntry,
    keys,
    keySet,
    keysIterator,
    keyStepper,
    values,
    valuesIterator,
    valueStepper,
  }
end MapWithDefault

object MapWithDefault:

  def empty[K, V](default: V): MapWithDefault[K, V] =
    MapWithDefault(Map.empty, default)
