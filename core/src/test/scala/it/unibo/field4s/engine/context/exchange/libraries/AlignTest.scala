package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.UnitTest
import it.unibo.field4s.engine.context.ValueTreeProbingContextMixin
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.Export
import it.unibo.field4s.language.AggregateFoundation
import it.unibo.field4s.language.libraries.All.{*, given}

trait AlignTest:
  this: UnitTest & ValueTreeProbingContextMixin & BasicFactoryMixin =>

  def testAlign(): Unit =
    def program(using BasicExchangeCalculusContext[Int]) =
      align(self % 2 == 0): () =>
        neighborValues(self)

    "align method" should "align domains" in:
      val result: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
        localId = 0,
        factory = factory,
        program = program,
      )
      val otherResult: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
          localId = 1,
          factory = factory,
          program = program,
      )
      result(0).prefixes shouldNot be(otherResult(1).prefixes)


