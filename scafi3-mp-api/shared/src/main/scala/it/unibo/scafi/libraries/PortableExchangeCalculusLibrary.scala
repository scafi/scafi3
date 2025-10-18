package it.unibo.scafi.libraries

import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.types.PortableTypes

/**
 * The portable library providing the exchange calculus primitive, `exchange`.
 */
trait PortableExchangeCalculusLibrary extends PortableLibrary:
  self: PortableTypes =>
  export it.unibo.scafi.language.xc.syntax.ExchangeSyntax
  import it.unibo.scafi.language.xc.syntax.ReturnSending as RetSend

  override type Language <: AggregateFoundation & ExchangeSyntax

  /**
   * This method is the main construct of the exchange calculus. It allows both to send and receive messages, and to
   * perform local computation. It allows sending an aggregate value as a message to neighbors, and to return a
   * different aggregate value as a result of the computation.
   *
   * <h2>Examples</h2>
   *
   * <h3>JavaScript</h3>
   *   - To send and return the same value: {{{lang.exchange(lang.Field.of(0), nvalue => returnSending(f(nvalue))}}}
   *   - To send and return different values:
   *     {{{lang.exchange(lang.Field.of(0), nvalue => returning(f(nvalue)).send(g(nvalue)))}}} or
   *     {{{lang.exchange(lang.Field.of(0), nvalue => ReturnSending(f(nvalue), g(nvalue)))}}}
   *
   * @param initial
   *   the initial aggregate value
   * @param f
   *   the function that takes the initial/received aggregate value and returns a new aggregate value or two aggregate
   *   values, one to be sent and one to be returned
   * @tparam Value
   *   the type of the aggregate value
   * @return
   *   the new aggregate value
   */
  @JSExport
  def exchange[Value](initial: SharedData[Value])(
      f: Function1[SharedData[Value], ReturnSending[SharedData[Value]]],
  ): SharedData[Value] = exchange_(initial)(f)

  inline def exchange_[Value](initial: SharedData[Value])(
      f: Function1[SharedData[Value], ReturnSending[SharedData[Value]]],
  ): SharedData[Value] = language.exchange(initial)(f(_).pipe(sd => RetSend(sd.returning, sd.sending)))
end PortableExchangeCalculusLibrary
