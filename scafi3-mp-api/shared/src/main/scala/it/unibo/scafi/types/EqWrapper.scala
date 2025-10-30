package it.unibo.scafi.types

import cats.kernel.Hash

/**
 * A wrapper class that provides equality and hashing based on a given `Hash` instance for type `T`. This is useful for
 * using instances of `T` as keys in hash-based collections.
 * @param value
 *   the value to be wrapped. Assumes that a `Hash[T]` instance is available in the implicit scope.
 * @tparam T
 *   the type of the value being wrapped.
 */
final class EqWrapper[T: Hash](val value: T):
  override def equals(obj: Any): Boolean = obj match
    case that: EqWrapper[T] @unchecked => Hash[T].eqv(this.value, that.value)
    case _ => false

  override def hashCode(): Int = Hash[T].hash(value)

  override def toString: String = s"EqWrapper($value)"
