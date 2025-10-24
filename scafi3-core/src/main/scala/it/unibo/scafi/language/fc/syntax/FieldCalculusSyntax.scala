package it.unibo.scafi.language.fc.syntax

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.CodableFromTo

trait FieldCalculusSyntax:
  this: AggregateFoundation =>

  /**
   * `neighborValues` sends a local value to <b>neighbours</b> and returns the aggregate value of the received messages.
   * @param expr
   *   the local value to send to neighbours
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of the local value
   * @return
   *   the aggregate value of the received messages
   */
  def neighborValues[Format, Value: CodableFromTo[Format]](expr: Value): SharedData[Value]

  /**
   * `evolve` <b>repeatedly</b> applies a function to an initial value for every execution round.
   * @param initial
   *   the initial value
   * @param evolution
   *   the function to apply
   * @tparam Value
   *   the type of the value
   * @return
   *   the value after the last application of the function
   */
  def evolve[Value](initial: Value)(evolution: Value => Value): Value

  /**
   * `share` computes a value by repeatedly applying a function to an initial value while <b>sharing</b> the result with
   * neighbours.
   * @param initial
   *   the initial value
   * @param shareAndReturning
   *   the function that returns the value to share and return
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of the value
   * @return
   *   the value after the last application of the function that has been shared with neighbours
   */
  def share[Format, Value: CodableFromTo[Format]](initial: Value)(shareAndReturning: SharedData[Value] => Value): Value
end FieldCalculusSyntax
