package it.unibo.scafi.language.exchange.bindings

import it.unibo.scafi.language.exchange.semantics.ExchangeCalculusSemantics
import it.unibo.scafi.language.syntax.ExchangeCalculusSyntax
import it.unibo.scafi.language.syntax.common.ReturnSending

/**
 * This trait enables the exchange syntax for the exchange calculus semantics.
 */
trait ExchangeBindings extends ExchangeCalculusSyntax:
  self: ExchangeCalculusSemantics =>

  override def exchange[T](initial: SharedData[T])(
      f: SharedData[T] => ReturnSending[SharedData[T]],
  ): SharedData[T] =
    xc(initial)(f.andThen(rs => (rs.returning, rs.sending)))
