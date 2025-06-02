package it.unibo.scafi.api

trait JVMTypes extends PortableTypes:

  override type Map[K, V] = collection.Map[K, V]
  override given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = Iso.id

  override type Tuple2[A, B] = (A, B)
  override given [A, B] => Iso[Tuple2[A, B], (A, B)] = Iso.id

  override type Function1[T1, R] = T1 => R
  override given [T1, R] => Conversion[Function1[T1, R], T1 => R] = _.apply
