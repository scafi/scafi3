package it.unibo.scafi.language.xc.semantic

import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.language.xc.syntax.ReturnSending.returning
import it.unibo.scafi.language.xc.syntax.ExchangeCalculusSyntax

/**
 * This trait witnesses the fact that the field calculus can be implemented by the exchange calculus.
 */
trait FieldCalculusViaExchangeSemantic extends FieldCalculusSyntax, ExchangeCalculusSyntax:
  this: ExchangeCalculus =>

  override def neighborValues[V](expr: V): SharedData[V] =
    exchange(expr)(nv => returning(nv) send expr)

  override def evolve[A](initial: A)(evolution: A => A): A =
    exchange[Option[A]](None)(nones =>
      val previousValue = nones(localId).getOrElse(initial)
      nones.set(localId, Some(evolution(previousValue))),
    )(localId).get

  override def share[A](initial: A)(shareAndReturning: SharedData[A] => A): A =
    exchange(initial)(nv => shareAndReturning(nv))(localId)
