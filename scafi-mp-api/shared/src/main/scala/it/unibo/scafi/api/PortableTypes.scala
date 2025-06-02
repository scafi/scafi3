package it.unibo.scafi.api

trait PortableTypes:
  export monocle.Iso
  export scala.scalajs.js.annotation.{ JSExport, JSExportAll }
  export it.unibo.scafi.utils.api.IsoUtils.given

  type Map[K, V]
  given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = compiletime.deferred

  type Tuple2[A, B]
  given [A, B] => Iso[Tuple2[A, B], (A, B)] = compiletime.deferred

  type Function1[T1, R]
  given [T1, R] => Conversion[Function1[T1, R], T1 => R] = compiletime.deferred
