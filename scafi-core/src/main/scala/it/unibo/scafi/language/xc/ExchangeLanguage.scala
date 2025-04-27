package it.unibo.scafi.language.xc

import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.language.xc.syntax.ReturnSending.returning
import it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }

/**
 * Exchange language provides the syntax for the exchange calculus main construct: `exchange`, but also provides the
 * [[FieldCalculusSyntax]] since its operators can be implemented in terms of exchange calculus.
 *
 * This trait requires the [[ExchangeCalculus]] trait to be mixed in, since it provides the implementation of the
 * exchange calculus.
 */
trait ExchangeLanguage extends ExchangeSyntax, FieldCalculusSyntax:
  self: ExchangeCalculus =>

  override def exchange[T](initial: SharedData[T])(
      f: SharedData[T] => ReturnSending[SharedData[T]],
  ): SharedData[T] = xc(initial)(f.andThen(rs => (rs.returning, rs.sending)))

  override def neighborValues[V](expr: V): SharedData[V] = exchange(expr)(nv => returning(nv) send expr)

  override def evolve[A](initial: A)(evolution: A => A): A = exchange[Option[A]](None)(nones =>
    val previousValue = nones(localId).getOrElse(initial)
    nones.set(localId, Some(evolution(previousValue))),
  )(localId).get

  override def share[A](initial: A)(shareAndReturning: SharedData[A] => A): A =
    exchange(initial)(nv => shareAndReturning(nv))(localId)
