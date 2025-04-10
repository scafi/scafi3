package it.unibo.scafi.language.xc.semantic

import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.language.xc.syntax.{ ExchangeCalculusSyntax, ReturnSending }

/**
 * This trait enables the exchange syntax for the exchange calculus semantics.
 */
trait ExchangeSemantic extends ExchangeCalculusSyntax:
  self: ExchangeCalculus =>

  override def exchange[T](initial: SharedData[T])(
      f: SharedData[T] => ReturnSending[SharedData[T]],
  ): SharedData[T] =
    xc(initial)(f.andThen(rs => (rs.returning, rs.sending)))
