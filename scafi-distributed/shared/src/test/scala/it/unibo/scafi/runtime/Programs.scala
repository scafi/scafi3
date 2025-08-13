package it.unibo.scafi.runtime

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.libraries.All.*
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.context.AggregateContext

trait Programs:

  type ID = Int

  given BinaryCodable[ID] = compiletime.deferred

  type Lang = AggregateContext { type DeviceId = ID } & AggregateFoundation & FieldBasedSharedData & ExchangeSyntax &
    BranchingSyntax

  def pingPong(using lang: Lang): lang.SharedData[ID] =
    exchange(0)(n => returnSending(n.map(_ + 1)))

  def exchangeWithRestrictions(using lang: Lang): Map[ID, Int] =
    branch(localId % 2 == 0)(
      exchange(100)(returnSending).neighborValues,
    )(
      exchange(200)(returnSending).neighborValues,
    )

  // def programs(using lang: Lang) = List(pingPong, exchangeWithRestrictions)
end Programs
