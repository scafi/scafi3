package it.unibo.scafi.experiments

import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.libraries.All.*
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.message.Codables.inMemory

object Experiments:

  @main def simple(): Unit =
    type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData

    def program(using Lang) =
      exchange(localId)(n => returnSending(n))

    val env = mooreGrid(3, 3, exchangeContextFactory)(program)
    env.cycleInOrder()
    env.status.foreach: (id, field) =>
      println(s"Node $id: $field")
    env.cycleInOrder()
    println("-" * 20)
    env.status.foreach: (id, field) =>
      println(s"Node $id: $field")
