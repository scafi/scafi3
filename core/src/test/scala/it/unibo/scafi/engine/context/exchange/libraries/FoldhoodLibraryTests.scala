package it.unibo.scafi.engine.context.exchange.libraries

import it.unibo.scafi.engine.context.{ ValueTreeProbingContextMixin, ValueTreeTestingNetwork }
import it.unibo.scafi.UnitTest
import it.unibo.scafi.collections.ValueTree
import it.unibo.scafi.engine.context.ContextFactory
import it.unibo.scafi.engine.context.common.InvocationCoordinate
import it.unibo.scafi.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.scafi.language.libraries.FoldhoodLibrary.*
import it.unibo.scafi.language.libraries.CommonLibrary.self
import it.unibo.scafi.engine.network.{ Export, Import }
import it.unibo.scafi.language.sensors.DistanceSensor

import cats.syntax.all.*

trait FoldhoodLibraryTests:
  this: UnitTest & ValueTreeProbingContextMixin =>

  def foldhoodSemantics(): Unit =
    class BasicExchangeCalculusContextWithHopDistance(
        self: Int,
        inboundMessages: Import[Int, BasicExchangeCalculusContext.ExportValue],
    ) extends BasicExchangeCalculusContext[Int](self, inboundMessages)
        with DistanceSensor[Int]:
      override def senseDistance: SharedData[Int] = device.map(id => if id == self then 0 else 1)

    val factory: ContextFactory[
      ValueTreeTestingNetwork[Int, InvocationCoordinate, Any],
      BasicExchangeCalculusContextWithHopDistance,
    ] =
      n => new BasicExchangeCalculusContextWithHopDistance(n.localId, n.received)

    var results: Map[Int, Int] = Map.empty

    def sum(x: Int, y: Int): Int = x + y

    def foldhoodingPlusProgram(using BasicExchangeCalculusContextWithHopDistance): Unit =
      val foldhoodResult = foldhoodWithoutSelf(0)(sum):
        nbr(self) + nbr("3").toInt + distances(using Numeric.IntIsIntegral)
      results += (self -> foldhoodResult)

    def foldhoodingProgram(using BasicExchangeCalculusContextWithHopDistance): Unit =
      val foldhoodResult = foldhood(0)(sum) { nbr(self) + nbr("3").toInt + distances(using Numeric.IntIsIntegral) }
      results += (self -> foldhoodResult)

    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 66,
      factory = factory,
      program = foldhoodingPlusProgram,
    )

    it should "evaluate foldhoodPlus expression only for self without neighbors" in:
      exportProbe.single._1 shouldBe 66
      results(66) shouldBe 69

    it should "evaluate foldhoodPlus expression for self and neighbors" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = foldhoodingPlusProgram,
        inboundMessages = Map(
          1 -> probe(
            localId = 1,
            factory = factory,
            program = foldhoodingPlusProgram,
          )(66),
          2 -> probe(
            localId = 2,
            factory = factory,
            program = foldhoodingPlusProgram,
          )(66),
          3 -> probe(
            localId = 3,
            factory = factory,
            program = foldhoodingPlusProgram,
          )(66),
          66 -> exportProbe(66),
        ),
      )
      exportProbe.size shouldBe 4
      results(66) shouldBe 87

    it should "not evaluate foldhood expression for self without neighbors" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = foldhoodingProgram,
      )
      exportProbe.single._1 shouldBe 66
      results(66) shouldBe 0

    it should "evaluate foldhood expression for self and neighbors" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = foldhoodingProgram,
        inboundMessages = Map(
          1 -> probe(
            localId = 1,
            factory = factory,
            program = foldhoodingProgram,
          )(66),
          2 -> probe(
            localId = 2,
            factory = factory,
            program = foldhoodingProgram,
          )(66),
          3 -> probe(
            localId = 3,
            factory = factory,
            program = foldhoodingProgram,
          )(66),
          66 -> probe(
            localId = 66,
            factory = factory,
            program = foldhoodingProgram,
          )(66),
        ),
      )
      exportProbe.size shouldBe 4
      results(66) shouldBe 18
  end foldhoodSemantics
end FoldhoodLibraryTests
