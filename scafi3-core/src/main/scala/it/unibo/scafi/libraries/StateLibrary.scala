package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax

/** Round-to-round state helpers built purely on `evolve`. No communication. */
object StateLibrary:

  /** Counts how many rounds this expression has been evaluated, starting at 1. */
  def roundCounter(using language: AggregateFoundation & FieldCalculusSyntax): Long =
    language.evolve(0L)(_ + 1)

  /** Latches the first value seen and keeps returning it forever. */
  def remember[T](value: => T)(using language: AggregateFoundation & FieldCalculusSyntax): T =
    language.evolve(value)(identity)

  /** Alias of [[remember]]. */
  def constant[T](value: => T)(using language: AggregateFoundation & FieldCalculusSyntax): T =
    remember(value)

  /** Keeps the last non-empty optional ever produced; `None` until the first `Some`. */
  def keep[T](expr: => Option[T])(using language: AggregateFoundation & FieldCalculusSyntax): Option[T] =
    language.evolve(Option.empty[T])(old => expr.orElse(old))

  /** Latches a boolean event to `true` forever once it first holds. */
  def keepTrue(expr: => Boolean)(using language: AggregateFoundation & FieldCalculusSyntax): Boolean =
    language.evolve(false)(old => old || expr)

  /** True exactly on rounds where `x` differs from the previous round; defaults `initially` to `true`. */
  def captureChange[T](x: T)(using language: AggregateFoundation & FieldCalculusSyntax)(using
      CanEqual[T, T],
  ): Boolean =
    captureChange(x, true)

  /**
   * True exactly on rounds where `x` differs from the previous round.
   * @param initially
   *   value to return on the very first round (no previous value exists)
   */
  def captureChange[T](x: T, initially: Boolean)(using language: AggregateFoundation & FieldCalculusSyntax)(using
      CanEqual[T, T],
  ): Boolean =
    language.evolve((Option.empty[T], initially)) { case (prev, _) =>
      (Some(x), prev.fold(initially)(_ != x))
    }._2

  /** `(#changes so far, changed-this-round)`; defaults `initially` to `true`. */
  def countChanges[T](x: T)(using language: AggregateFoundation & FieldCalculusSyntax)(using
      CanEqual[T, T],
  ): (Long, Boolean) =
    countChanges(x, true)

  /**
   * `(#changes so far, changed-this-round)`.
   * @param initially
   *   whether the first round is counted as a change
   */
  def countChanges[T](x: T, initially: Boolean)(using language: AggregateFoundation & FieldCalculusSyntax)(using
      CanEqual[T, T],
  ): (Long, Boolean) =
    val state = language.evolve((Option.empty[T], 0L, initially)) { case (prev, count, _) =>
      val changed = prev.fold(initially)(_ != x)
      (Some(x), if changed then count + 1 else count, changed)
    }
    (state._2, state._3)

  /** Returns the input delayed by exactly one round; echoes the current value on the first round. */
  def delay[T](value: T)(using language: AggregateFoundation & FieldCalculusSyntax): T =
    language.evolve((value, value)) { case (_, prev) => (prev, value) }._1

  /** Rising edge: `true` on the round `value` goes `false` → `true`. */
  def goesUp(value: Boolean)(using language: AggregateFoundation & FieldCalculusSyntax): Boolean =
    !delay(value) && value

  /** Falling edge: `true` on the round `value` goes `true` → `false`. */
  def goesDown(value: Boolean)(using language: AggregateFoundation & FieldCalculusSyntax): Boolean =
    delay(value) && !value

  /** `Some(expr)` on the very first round, `None` thereafter. */
  def once[T](expr: => T)(using language: AggregateFoundation & FieldCalculusSyntax): Option[T] =
    language.evolve((true, Option.empty[T])) { case (isFirst, _) =>
      (false, if isFirst then Some(expr) else None)
    }._2

end StateLibrary
