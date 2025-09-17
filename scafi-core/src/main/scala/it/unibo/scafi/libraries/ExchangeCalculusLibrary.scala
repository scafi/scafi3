package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }
import it.unibo.scafi.message.CodableFromTo

/**
 * This library provides the exchange calculus primitive, `exchange`.
 */
object ExchangeCalculusLibrary:
  export ReturnSending.{ *, given }

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
   *   [[ReturnSending]] [[ExchangeSyntax.exchange]]
   */
  def exchange[Format, Value: CodableFromTo[Format]](using
      language: AggregateFoundation & ExchangeSyntax,
  )(initial: language.SharedData[Value])(
      f: language.SharedData[Value] => ReturnSending[language.SharedData[Value]],
  ): language.SharedData[Value] = language.exchange(initial)(f)
end ExchangeCalculusLibrary
