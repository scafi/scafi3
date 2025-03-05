package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.engine.context.{ ValueTreeProbingContextMixin, ValueTreeTestingNetwork }
import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.UnitTest
import it.unibo.field4s.engine.context.ContextFactory
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.{ Export, Import }
import it.unibo.field4s.language.libraries.All.{ *, given }
import it.unibo.field4s.language.sensors.DistanceSensor

import cats.syntax.all.*

trait GradientTests:
  this: UnitTest & ValueTreeProbingContextMixin & BasicFactoryMixin =>

  private val epsilon = 0.0001

  private def gradientWithDistanceSensorSemantics(): Unit =
    class ContextWithDistanceSensor(self: Int, inboundMessages: Import[Int, BasicExchangeCalculusContext.ExportValue])
        extends BasicExchangeCalculusContext[Int](self, inboundMessages)
        with DistanceSensor[Double]:
      override def senseDistance: SharedData[Double] = device.map(_.toDouble)

    val factory: ContextFactory[ValueTreeTestingNetwork[Int, InvocationCoordinate, Any], ContextWithDistanceSensor] =
      n => new ContextWithDistanceSensor(n.localId, n.receive())

    var gradientValue: Double = 0.0
    def gradientProgram(using ContextWithDistanceSensor): Unit =
      gradientValue = sensorDistanceTo(self == 2)

    val exportProbeSource: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 2,
      factory = factory,
      program = gradientProgram,
    )
    it should "return 0 for source" in:
      gradientValue shouldBe 0.0 +- epsilon
      exportProbeSource(2).single._2.as[Double] shouldBe 0.0 +- epsilon
      exportProbeSource(3).single._2.as[Double] shouldBe 0.0 +- epsilon

    it should "return the measured distance for source neighbours" in:
      val exportProbeCloseToSource: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
        localId = 3,
        factory = factory,
        program = gradientProgram,
        inboundMessages = Map(
          2 -> exportProbeSource(3),
        ),
      )
      gradientValue shouldBe 2.0 +- epsilon
      exportProbeCloseToSource(2).single._2.as[Double] shouldBe 2.0 +- epsilon

  end gradientWithDistanceSensorSemantics

  def gradientSemantics(): Unit =
    var gradientValue: Double = 0.0
    def gradientProgram(using BasicExchangeCalculusContext[Int]): Unit =
      gradientValue = distanceTo(self == 0, 5.0)
    val exportProbeSource: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 0,
      factory = factory,
      program = gradientProgram,
    )
    val sourceGradient: Double = gradientValue
    val exportProbeCloseToSource = probe(
      localId = 1,
      factory = factory,
      program = gradientProgram,
      inboundMessages = Map(
        0 -> exportProbeSource(1),
      ),
    )
    val closeToSourceGradient: Double = gradientValue
    val exportProbeFarToSource = probe(
      localId = 2,
      factory = factory,
      program = gradientProgram,
      inboundMessages = Map(
        1 -> exportProbeCloseToSource(2),
      ),
    )
    val farFromSourceGradient: Double = gradientValue

    it should "return 0 for source" in:
      sourceGradient shouldBe 0.0 +- epsilon
      for i <- 0 to 1 do exportProbeSource(i).single._2.as[Double] shouldBe 0.0 +- epsilon

    it should "return infinity for non-source isolated devices" in:
      val exportProbe = probe(localId = 1, factory = factory, program = gradientProgram)
      gradientValue shouldBe Double.PositiveInfinity
      exportProbe(1).single._2.as[Double] shouldBe Double.PositiveInfinity

    it should "return the distance from source for source neighbours" in:
      closeToSourceGradient shouldBe 5.0 +- epsilon
      for i <- 0 to 3 do exportProbeCloseToSource(i).single._2.as[Double] shouldBe 5.0 +- epsilon

    it should "sum the distance from source for far devices" in:
      farFromSourceGradient shouldBe 10.0 +- epsilon
      exportProbeFarToSource(1).single._2.as[Double] shouldBe 10.0 +- epsilon
      exportProbeFarToSource(3).single._2.as[Double] shouldBe 10.0 +- epsilon

    it should "return the minimum distance for others" in:
      val exportProbe = probe(
        localId = 3,
        factory = factory,
        program = gradientProgram,
        inboundMessages = Map(
          1 -> exportProbeCloseToSource(3),
          2 -> exportProbeFarToSource(3),
        ),
      )
      gradientValue shouldBe 10.0 +- epsilon
      for i <- 1 to 2 do exportProbe(i).single._2.as[Double] shouldBe 10.0 +- epsilon

    "using the distance sensor" should behave like gradientWithDistanceSensorSemantics()
  end gradientSemantics
end GradientTests
