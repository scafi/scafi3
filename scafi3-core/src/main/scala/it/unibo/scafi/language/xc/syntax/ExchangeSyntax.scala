package it.unibo.scafi.language.xc.syntax

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.CodableFromTo

/**
 * This trait provides the syntax for the exchange calculus main construct: `exchange`.
 */
trait ExchangeSyntax:
  this: AggregateFoundation =>

  /**
   * This method is the main construct of the exchange calculus. It allows both to send and receive messages, and to
   * perform local computation. It allows to send an aggregate value as a message to neighbours, and to return a
   * different aggregate value as a result of the computation.
   *
   * <h3>Examples</h3>
   *
   * <h4>To send and return the same value</h4> {{{exchange(0)(value => f(value))}}}
   * {{{exchange(0)(value => returnSending(f(value)))}}}
   *
   * <h4>To send and return different values</h4> {{{exchange(0)(value => (f(value), f2(value)))}}}
   * {{{exchange(0)(value => returning (f(value)) send f2(value))}}}
   * {{{exchange(0)(value => returning(f(value)).send(f2(value)))}}}
   * {{{exchange(0)(value => ReturnSending(f(value), f2(value)))}}}
   *
   * @param initial
   *   the initial aggregate value
   * @param f
   *   the function that takes the initial/received aggregate value and returns a new aggregate value or two aggregate
   *   values, one to be sent and one to be returned
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of the aggregate value
   * @return
   *   the new aggregate value
   * @see
   *   [[ReturnSending]]
   */
  def exchange[Format, Value: CodableFromTo[Format]](initial: SharedData[Value])(
      f: SharedData[Value] => ReturnSending[SharedData[Value]],
  ): SharedData[Value]
end ExchangeSyntax
