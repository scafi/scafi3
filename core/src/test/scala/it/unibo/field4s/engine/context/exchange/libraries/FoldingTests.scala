package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.engine.context.ValueTreeProbingContextMixin
import it.unibo.field4s.UnitTest
import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.Export
import it.unibo.field4s.language.libraries.All.{ *, given }

trait FoldingTests:
  this: UnitTest & ValueTreeProbingContextMixin & BasicFactoryMixin =>

  def foldingSemantics(): Unit =
    var foldingResult: Long = 0
    var neighbouringFoldingResult: Long = 0
    def foldingProgram(using BasicExchangeCalculusContext[Int]): Unit =
      branch(self < 10) {
        foldingResult = neighborValues(self).fold(1)(_ * _)
        neighbouringFoldingResult = neighborValues(self).nfold(1)(_ * _)
      } {}

    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 2,
      factory = factory,
      program = foldingProgram,
    )

    it should "correctly fold the values of neighbours with fold" in:
      foldingResult shouldBe 2
    it should "correctly fold the values of neighbours excluding self with nfold" in:
      neighbouringFoldingResult shouldBe 1

    it should "correctly fold the values of neighbours with fold when there are multiple neighbours" in:
      exportProbe = probe(
        localId = 3,
        factory = factory,
        program = foldingProgram,
        inboundMessages = Map(
          2 -> exportProbe(3),
          7 -> probe(
            localId = 7,
            factory = factory,
            program = foldingProgram,
            inboundMessages = Map(7 -> ValueTree.empty, 3 -> ValueTree.empty),
          )(3),
        ),
      )
      foldingResult shouldBe 42
    it should "correctly fold the values of neighbours excluding self with nfold when there are multiple neighbours" in:
      neighbouringFoldingResult shouldBe 14
  end foldingSemantics
end FoldingTests
