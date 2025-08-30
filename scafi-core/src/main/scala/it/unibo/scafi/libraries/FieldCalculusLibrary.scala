package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.CodableFromTo

/**
 * This library provides the field calculus primitives: `neighborValues`, `rep`, and `share`.
 */
object FieldCalculusLibrary:

  /**
   * `neighborValues` sends a local value to <b>neighbours</b> and returns the aggregate value of the received messages.
   *
   * @param value
   *   the local value to send to neighbours
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of the local value
   * @return
   *   the aggregate value of the received messages
   * @see
   *   [[FieldCalculusSyntax.neighborValues]]
   */
  def neighborValues[Format, Value: CodableFromTo[Format]](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(value: Value): language.SharedData[Value] = language.neighborValues(value)

  /**
   * `rep` <b>repeatedly</b> applies a function to an initial value for every execution round.
   *
   * @param init
   *   the initial value
   * @param f
   *   the function to apply
   * @tparam Value
   *   the type of the value
   * @return
   *   the value after the last application of the function
   * @see
   *   [[FieldCalculusSyntax.evolve]]
   */
  def evolve[Value](using language: AggregateFoundation & FieldCalculusSyntax)(init: Value)(f: Value => Value): Value =
    language.evolve(init)(f)

  /**
   * `share` computes a value by repeatedly applying a function to an initial value while <b>sharing</b> the result with
   * neighbours.
   *
   * @param init
   *   the initial value
   * @param f
   *   the function that returns the value to share and return
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of the value
   * @return
   *   the value after the last application of the function that has been shared with neighbours
   * @see
   *   [[FieldCalculusSyntax.share]]
   */
  def share[Format, Value: CodableFromTo[Format]](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(init: Value)(
      f: language.SharedData[Value] => Value,
  ): Value = language.share(init)(f)
end FieldCalculusLibrary
