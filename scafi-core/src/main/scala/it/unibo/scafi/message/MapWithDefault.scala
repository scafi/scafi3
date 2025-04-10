package it.unibo.scafi.message

private final case class MapWithDefault[K, +V](private val underline: Map[K, V], default: V) extends Map[K, V]:
  override def removed(key: K): Map[K, V] = underline.removed(key)

  override def updated[V1 >: V](key: K, value: V1): Map[K, V1] = underline.updated(key, value)

  override def get(key: K): Option[V] = Some(underline.getOrElse(key, default))

  override def iterator: Iterator[(K, V)] = underline.iterator
