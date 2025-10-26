package it.unibo.scafi.language.xc

import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }
import it.unibo.scafi.language.xc.syntax.ReturnSending.returning
import it.unibo.scafi.message.{ CodableFromTo, Codables }

/**
 * Exchange language provides the syntax for the exchange calculus main construct: `exchange`, but also provides the
 * [[FieldCalculusSyntax]] since its operators can be implemented in terms of exchange calculus.
 *
 * This trait requires the [[ExchangeCalculus]] trait to be mixed in, since it provides the implementation of the
 * exchange calculus.
 */
trait ExchangeLanguage extends ExchangeSyntax, FieldCalculusSyntax:
  this: ExchangeCalculus =>

  override def exchange[Format, Value: CodableFromTo[Format]](initial: SharedData[Value])(
      f: SharedData[Value] => ReturnSending[SharedData[Value]],
  ): SharedData[Value] = xc(initial)(f.andThen(rs => (rs.returning, rs.sending)))

  override def neighborValues[Format, Value: CodableFromTo[Format]](expr: Value): SharedData[Value] =
    exchange(expr)(nv => returning(nv) send expr)

  override def evolve[Value](initial: Value)(evolution: Value => Value): Value =
    // `exchange` is called only to update the self-value: `None` is shared with neighbors, so an in-memory
    // codec is enough; non-in-memory network managers will ignore it since it is not serialized.
    exchange(None)(nones =>
      val previousValue = nones(localId).getOrElse(initial)
      nones.set(localId, Some(evolution(previousValue))),
    )(using Codables.forInMemoryCommunications)(localId).get

  override def share[Format, Value: CodableFromTo[Format]](initial: Value)(
      shareAndReturning: SharedData[Value] => Value,
  ): Value = exchange(initial)(nv => shareAndReturning(nv))(localId)
end ExchangeLanguage
