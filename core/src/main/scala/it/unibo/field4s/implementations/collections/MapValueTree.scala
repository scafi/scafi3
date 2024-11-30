package it.unibo.field4s.implementations.collections

import scala.annotation.targetName

import it.unibo.field4s.collections.ValueTree

case class MapValueTree[N, +V](underlying: Map[Seq[N], V]) extends ValueTree[N, V]:
  override def contains(seq: Seq[N]): Boolean = underlying.contains(seq)

  override def containsPrefix(seq: Iterable[N]): Boolean =
    underlying.exists(_._1.startsWith(seq))

  override def get(seq: Seq[N]): Option[V] = underlying.get(seq)

  override def mapValues[V1](f: (Seq[N], V) => V1): ValueTree[N, V1] =
    MapValueTree(
      underlying.map((k, v) => k -> f(k, v)),
    )

  override def mapNodes[N1](f: N => N1): ValueTree[N1, V] = MapValueTree(
    underlying.map((k, v) => k.map(f) -> v),
  )

  override def map[N1, V1](f: (Seq[N], V) => (Seq[N1], V1)): ValueTree[N1, V1] =
    MapValueTree(
      underlying.map((k, v) => f(k, v)),
    )

  override def filter(f: (Seq[N], V) => Boolean): ValueTree[N, V] =
    MapValueTree(underlying.filter((k, v) => f(k, v)))

  override def flatMap[N1, V1](
      f: (Seq[N], V) => IterableOnce[(Seq[N1], V1)],
  ): ValueTree[N1, V1] = MapValueTree(
    underlying.flatMap((k, v) => f(k, v).iterator),
  )

  override def remove(seq: Seq[N]): ValueTree[N, V] = MapValueTree(
    underlying - seq,
  )

  override def removePrefix(seq: Iterable[N]): ValueTree[N, V] = MapValueTree(
    underlying.filterNot(_._1.startsWith(seq)),
  )

  override def update[V1 >: V](seq: Seq[N], value: V1): ValueTree[N, V1] =
    MapValueTree(underlying.updated(seq, value))

  override def concat[V1 >: V](other: ValueTree[N, V1]): ValueTree[N, V1] =
    MapValueTree(underlying ++ other.iterator)

  override def partition(
      f: (Seq[N], V) => Boolean,
  ): (ValueTree[N, V], ValueTree[N, V]) =
    val (left, right) = underlying.partition((k, v) => f(k, v))
    (MapValueTree(left), MapValueTree(right))

  override def iterator: Iterator[(Seq[N], V)] = underlying.iterator
end MapValueTree

object MapValueTree extends ValueTree.Factory[MapValueTree]:
  override def empty[N, V]: MapValueTree[N, V] = MapValueTree(Map.empty)

  override def apply[N, V](elems: (Seq[N], V)*): MapValueTree[N, V] =
    MapValueTree(elems.toMap)

  @targetName("merge")
  override def apply[N, V](elems: ValueTree[N, V]*): MapValueTree[N, V] =
    MapValueTree(elems.flatMap(_.iterator).toMap)
