package it.unibo.scafi.language.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.syntax.ExchangeCalculusSyntax
import it.unibo.scafi.language.syntax.common.ReturnSending

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
   * {{{exchange(0)(value => retsend(f(value)))}}} <h4>To send and return different values</h4>
   * {{{exchange(0)(value => (f(value), f2(value)))}}} {{{exchange(0)(value => ret (f(value)) send f2(value))}}}
   * {{{exchange(0)(value => ret(f(value)).send(f2(value)))}}} {{{exchange(0)(value => RetSend(f(value), f2(value)))}}}
   *
   * @param initial
   *   the initial aggregate value
   * @param f
   *   the function that takes the initial/received aggregate value and returns a new aggregate value or two aggregate
   *   values, one to be sent and one to be returned
   * @tparam T
   *   the type of the aggregate value
   * @return
   *   the new aggregate value
   * @see
   *   [[ReturnSending]] [[ExchangeCalculusSyntax.exchange]]
   */
  def exchange[T](using
      language: AggregateFoundation & ExchangeCalculusSyntax,
  )(initial: language.SharedData[T])(
      f: language.SharedData[T] => ReturnSending[language.SharedData[T]],
  ): language.SharedData[T] = language.exchange(initial)(f)
end ExchangeCalculusLibrary
