package it.unibo.scafi.libraries

/**
 * This trait defines portable types among different platforms, along with their Scala conversions. Actual
 * platform-specific implementations of the multi-platform API should incarnate these types with the appropriate
 * platform-specific ones and provide, for each of them, the required Scala conversions.
 */
trait PortableTypes:
  export monocle.Iso
  export scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSExportTopLevel }
  export it.unibo.scafi.utils.libraries.IsoUtils.given

  /**
   * A portable Map that can be used across different platforms.
   */
  type Map[K, V]

  /**
   * Portable maps are isomorphic to Scala's `collection.Map`.
   */
  given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = compiletime.deferred

  /**
   * A portable tuple of 2 elements that can be used across different platforms.
   */
  type Tuple2[A, B]

  /**
   * Portable tuples of 2 elements are isomorphic to Scala's `(A, B)`.
   */
  given [A, B] => Iso[Tuple2[A, B], (A, B)] = compiletime.deferred

  /**
   * A portable 1-argument function type that can be used across different platforms.
   */
  type Function1[T1, R]

  /**
   * Portable functions at 1-argument can be converted to Scala's `T1 => R`.
   */
  given [T1, R] => Conversion[Function1[T1, R], T1 => R] = compiletime.deferred
end PortableTypes
