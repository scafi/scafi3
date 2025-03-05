package it.unibo.field4s.language.semantics.exchange.syntaxes

import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics
import it.unibo.field4s.language.syntax.{ ExchangeCalculusSyntax, FieldCalculusSyntax }
import it.unibo.field4s.language.syntax.common.ReturnSending.returning

/**
 * This trait witnesses the fact that the field calculus can be implemented by the exchange calculus.
 */
trait FieldCalculusByExchangeSemantics extends FieldCalculusSyntax:
  this: ExchangeCalculusSemantics & ExchangeCalculusSyntax =>

  override def neighborValues[V](expr: V): AggregateValue[V] =
    exchange(expr)(nv => returning(nv) send expr)

  override def evolve[A](init: A)(f: A => A): A =
    exchange[Option[A]](None)(nones =>
      val previousValue = nones(self).getOrElse(init)
      nones.set(self, Some(f(previousValue))),
    )(self).get

  override def share[A](init: A)(f: AggregateValue[A] => A): A =
    exchange(init)(nv => f(nv))(self)
