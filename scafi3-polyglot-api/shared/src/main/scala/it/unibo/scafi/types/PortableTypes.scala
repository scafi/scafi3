package it.unibo.scafi.types

import scala.concurrent.Future

/**
 * This trait defines portable types among different platforms, along with their Scala conversions. Actual
 * platform-specific implementations of the multi-platform API should incarnate these types with the appropriate
 * platform-specific ones and provide, for each of them, the required Scala conversions.
 */
trait PortableTypes:
  export scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSExportTopLevel }
  export it.unibo.scafi.utils.libraries.Iso
  export it.unibo.scafi.utils.libraries.Iso.given

  /** A portable Map that can be used across different platforms. */
  type Map[K, V]

  /** Portable maps are isomorphic to Scala's `collection.Map`. */
  given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = compiletime.deferred

  type Seq[V]
  given [V] => Iso[Seq[V], collection.Seq[V]] = compiletime.deferred

  /**
   * A portable type representing a blocking computation that will eventually produce a value of type `T` that can be
   * used both in synchronous and asynchronous platforms where blocking is not possible.
   */
  type Outcome[T]

  /** Outcomes are isomorphic to Scala's `Future`. */
  given [T] => Iso[Outcome[T], Future[T]] = compiletime.deferred

  /** A portable 0-argument function type that can be used across different platforms. */
  type Function0[R]

  /** Portable functions at 0-argument can be converted to Scala's `() => R`. */
  given toScalaFunction0[R]: Conversion[Function0[R], () => R]

  /** A portable 1-argument function type that can be used across different platforms. */
  type Function1[T1, R]

  /** Portable functions at 1-argument can be converted to Scala's `T1 => R`. */
  given toScalaFunction1[T1, R]: Conversion[Function1[T1, R], T1 => R]

  /** A portable 2-argument function type that can be used across different platforms. */
  type Function2[T1, T2, R]

  /** Portable functions at 2-arguments can be converted to Scala's `(T1, T2) => R`. */
  given toScalaFunction2[T1, T2, R]: Conversion[Function2[T1, T2, R], (T1, T2) => R]

end PortableTypes
