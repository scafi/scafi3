package it.unibo.scafi.libraries

import scala.concurrent.Future

/**
 * This trait defines portable types among different platforms, along with their Scala conversions. Actual
 * platform-specific implementations of the multi-platform API should incarnate these types with the appropriate
 * platform-specific ones and provide, for each of them, the required Scala conversions.
 */
trait PortableTypes:
  export monocle.Iso
  export scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSExportTopLevel }
  export it.unibo.scafi.utils.libraries.IsoUtils.given

  /** A portable Map that can be used across different platforms. */
  type Map[K, V]

  /** Portable maps are isomorphic to Scala's `collection.Map`. */
  given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = compiletime.deferred

  /** A portable tuple of 2 elements that can be used across different platforms. */
  type Tuple2[A, B]

  /** Portable tuples of 2 elements are isomorphic to Scala's `(A, B)`. */
  given [A, B] => Iso[Tuple2[A, B], (A, B)] = compiletime.deferred

  /** A portable 0-argument function type that can be used across different platforms. */
  type Function0[R]

  /** Portable functions at 0-argument can be converted to Scala's `() => R`. */
  given [R] => Conversion[Function0[R], () => R] = compiletime.deferred

  /** A portable 1-argument function type that can be used across different platforms. */
  type Function1[T1, R]

  /** Portable functions at 1-argument can be converted to Scala's `T1 => R`. */
  given [T1, R] => Conversion[Function1[T1, R], T1 => R] = compiletime.deferred

  /**
   * A portable type representing a blocking computation that will eventually produce a value of type `T` that can be
   * used both in synchronous and asynchronous platforms where blocking is not possible.
   */
  type Outcome[T]

  /** Outcomes are isomorphic to Scala's `Future`. */
  given [T] => Iso[Outcome[T], Future[T]] = compiletime.deferred
end PortableTypes
