package it.unibo.scafi.engine.context.exchange.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.engine.context.ValueTreeProbingContextMixin
import it.unibo.scafi.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.scafi.engine.network.Export
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.libraries.All.{ *, given }

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
end AlignTest
