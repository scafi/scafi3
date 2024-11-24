package it.unibo.field4s.language.foundation

import it.unibo.field4s.abstractions.{ Aggregate, Liftable }
import it.unibo.field4s.collections.SafeIterable

trait AggregateFoundation:
  type AggregateValue[T] <: SafeIterable[T]

  /**
   * Aggregate values can be iterated also by ignoring the self value.
   */
  given aggregate: Aggregate[AggregateValue]

  /**
   * Aggregate values can be composed and mapped.
   */
  given liftable: Liftable[AggregateValue]
