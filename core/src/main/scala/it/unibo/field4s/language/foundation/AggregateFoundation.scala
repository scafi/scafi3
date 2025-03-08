package it.unibo.field4s.language.foundation

import it.unibo.field4s.abstractions.SharedDataOps
import it.unibo.field4s.collections.SafeIterable

import cats.Applicative

trait AggregateFoundation:
  type SharedData[T] <: SafeIterable[T]

  /**
   * Aggregate values can be iterated also by ignoring the self value.
   */
  given aggregate: SharedDataOps[SharedData] = scala.compiletime.deferred

  /**
   * Aggregate values can be composed and mapped.
   */
  given liftable: Applicative[SharedData] = scala.compiletime.deferred
