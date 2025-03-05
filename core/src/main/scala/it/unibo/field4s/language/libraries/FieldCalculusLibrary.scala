package it.unibo.field4s.language.libraries

import it.unibo.field4s.language.foundation.AggregateFoundation
import it.unibo.field4s.language.syntax.FieldCalculusSyntax

/**
 * This library provides the field calculus primitives: `neighborValues`, `rep`, and `share`.
 */
object FieldCalculusLibrary:

  /**
   * `neighborValues` sends a local value to <b>neighbours</b> and returns the aggregate value of the received messages.
   *
   * @param value
   *   the local value to send to neighbours
   * @tparam Value
   *   the type of the local value
   * @return
   *   the aggregate value of the received messages
   * @see
   *   [[FieldCalculusSyntax.neighborValues]]
   */
  def neighborValues[Value](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(value: Value): language.AggregateValue[Value] =
    language.neighborValues(value)

  /**
   * `rep` <b>repeatedly</b> applies a function to an initial value for every execution round.
   *
   * @param init
   *   the initial value
   * @param f
   *   the function to apply
   * @tparam A
   *   the type of the value
   * @return
   *   the value after the last application of the function
   * @see
   *   [[FieldCalculusSyntax.evolve]]
   */
  def evolve[A](using language: AggregateFoundation & FieldCalculusSyntax)(init: A)(f: A => A): A =
    language.evolve(init)(f)

  /**
   * `share` computes a value by repeatedly applying a function to an initial value while <b>sharing</b> the result with
   * neighbours.
   *
   * @param init
   *   the initial value
   * @param f
   *   the function that returns the value to share and return
   * @tparam A
   *   the type of the value
   * @return
   *   the value after the last application of the function that has been shared with neighbours
   * @see
   *   [[FieldCalculusSyntax.share]]
   */
  def share[A](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(init: A)(
      f: language.AggregateValue[A] => A,
  ): A = language.share(init)(f)
end FieldCalculusLibrary
