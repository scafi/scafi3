package it.unibo.scafi.libraries

/**
 * Provides JavaScript-specific implementations for the portable types.
 */
trait JSTypes extends PortableTypes:
  import scalajs.js

  override type Map[K, V] = js.Map[K, V]
  override given [K, V] => Iso[Map[K, V], collection.Map[K, V]] =
    Iso[Map[K, V], collection.Map[K, V]](_.toMap)(m => js.Map(m.toSeq*))

  override type Tuple2[A, B] = js.Tuple2[A, B]
  override given [A, B] => Iso[Tuple2[A, B], (A, B)] = Iso[Tuple2[A, B], (A, B)](identity)(identity)

  override type Function1[T1, R] = js.Function1[T1, R]
  override given [T1, R] => Conversion[Function1[T1, R], T1 => R] = _.apply
