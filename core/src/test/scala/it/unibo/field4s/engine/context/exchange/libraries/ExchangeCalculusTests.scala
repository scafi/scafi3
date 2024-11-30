package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.engine.context.{ ValueTreeProbingContextMixin, ValueTreeTestingNetwork }
import it.unibo.field4s.UnitTest
import it.unibo.field4s.collections.{ MapWithDefault, ValueTree }
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.ContextFactory
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.Export
import it.unibo.field4s.language.libraries.All.{ *, given }

trait ExchangeCalculusTests:
  this: UnitTest & ValueTreeProbingContextMixin & BasicFactoryMixin =>

  def exchangeSemantics(): Unit =
    var neighbours: Set[Int] = Set.empty
    def exchangingProgram(using BasicExchangeCalculusContext[Int]): Unit =
      assert(exchange(self)(ids =>
        neighbours = ids.toSet
        ret(ids.map(_ + 1)) send ids,
      ).toSet == neighbours.map(_ + 1)) // assert the ret/send semantics
    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 142,
      factory = factory,
      program = exchangingProgram,
      inboundMessages = Map(1000 -> ValueTree.empty), // unaligned devices should be ignored
    )
    it should "exchange with self if the device is alone after reboot" in:
      neighbours shouldBe Set(142) // the device just rebooted and only sees themselves
      exportProbe.map(_._1) should contain(142)
      exportProbe(142).single._1.size shouldBe 1
    it should "exchange with self if the device is alone" in:
      exportProbe = probe(
        localId = 142,
        factory = factory,
        program = exchangingProgram,
        inboundMessages = Map(142 -> exportProbe(142)),
      )
      neighbours shouldBe Set(142) // the device is still alone
      exportProbe.single._1 shouldBe 142
      exportProbe(142).single._1.size shouldBe 1
    it should "exchange with neighbours" in:
      val messageForNewNeighbour: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
        localId = 142,
        factory = factory,
        program = exchangingProgram,
        inboundMessages = Map(142 -> exportProbe(142)),
      )
      exportProbe = probe(
        localId = 0,
        factory = factory,
        program = exchangingProgram,
        inboundMessages = Map(142 -> messageForNewNeighbour(0)),
      )
      neighbours shouldBe Set(0, 142)
      exportProbe = probe(
        localId = 0,
        factory = factory,
        program = exchangingProgram,
        inboundMessages = Map(0 -> exportProbe(0), 142 -> messageForNewNeighbour(0)),
      )
      neighbours shouldBe Set(0, 142) // the device sees themselves after self messaging (memory)
  end exchangeSemantics
end ExchangeCalculusTests
