package it.unibo.field4s.language.semantics.exchange.syntaxes

import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics
import it.unibo.field4s.language.syntax.ExchangeCalculusSyntax
import it.unibo.field4s.language.syntax.common.ReturnSending

/**
 * This trait enables the exchange syntax for the exchange calculus semantics.
 */
trait ExchangeSemantics extends ExchangeCalculusSyntax:
  self: ExchangeCalculusSemantics =>

  override def exchange[T](initial: SharedData[T])(
    f: SharedData[T] => ReturnSending[SharedData[T]],
  ): SharedData[T] =
    xc(initial)(f.andThen(rs => (rs.returning, rs.sending)))
