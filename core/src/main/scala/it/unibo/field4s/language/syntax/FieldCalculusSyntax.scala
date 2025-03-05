package it.unibo.field4s.language.syntax

import it.unibo.field4s.language.foundation.AggregateFoundation

trait FieldCalculusSyntax:
  self: AggregateFoundation =>

  /**
   * `nbr` sends a local value to <b>neighbours</b> and returns the aggregate value of the received messages.
   * @param expr
   *   the local value to send to neighbours
   * @tparam Value
   *   the type of the local value
   * @return
   *   the aggregate value of the received messages
   */
  def neighborValues[Value](expr: Value): AggregateValue[Value]

  /**
   * `rep` <b>repeatedly</b> applies a function to an initial value for every execution round.
   * @param init
   *   the initial value
   * @param f
   *   the function to apply
   * @tparam A
   *   the type of the value
   * @return
   *   the value after the last application of the function
   */
  def evolve[A](init: A)(f: A => A): A

  /**
   * `share` computes a value by repeatedly applying a function to an initial value while <b>sharing</b> the result with
   * neighbours.
   * @param init
   *   the initial value
   * @param f
   *   the function that returns the value to share and return
   * @tparam A
   *   the type of the value
   * @return
   *   the value after the last application of the function that has been shared with neighbours
   */
  def share[A](init: A)(f: AggregateValue[A] => A): A
end FieldCalculusSyntax
