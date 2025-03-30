package it.unibo.scafi.language.exchange.bindings

import it.unibo.scafi.language.exchange.semantics.ExchangeCalculusSemantics
import it.unibo.scafi.language.syntax.{ ExchangeCalculusSyntax, FieldCalculusSyntax }
import it.unibo.scafi.language.syntax.common.ReturnSending.returning

/**
 * This trait witnesses the fact that the field calculus can be implemented by the exchange calculus.
 */
trait FieldCalculusByExchangeBindings extends FieldCalculusSyntax, ExchangeCalculusSyntax:
  this: ExchangeCalculusSemantics =>

  override def neighborValues[V](expr: V): SharedData[V] =
    exchange(expr)(nv => returning(nv) send expr)

  override def evolve[A](initial: A)(evolution: A => A): A =
    exchange[Option[A]](None)(nones =>
      val previousValue = nones(self).getOrElse(initial)
      nones.set(self, Some(evolution(previousValue))),
    )(self).get

  override def share[A](initial: A)(shareAndReturning: SharedData[A] => A): A =
    exchange(initial)(nv => shareAndReturning(nv))(self)
