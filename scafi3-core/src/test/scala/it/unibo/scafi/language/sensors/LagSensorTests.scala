package it.unibo.scafi.language.sensors

import scala.concurrent.duration.*

import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.sensors.LagSensor

class LagSensorTests extends UnitTest:

  given CanEqual[FiniteDuration, FiniteDuration] = CanEqual.derived

  type Language = AggregateFoundation & LagSensor[FiniteDuration]

  val mockedLags: Seq[FiniteDuration] = Seq(0.millis, 10.millis, 20.millis, 30.millis)

  given lang: Language = new AggregateFoundationMock with LagSensor[FiniteDuration]:
    override def senseLag: MockAggregate[FiniteDuration] = MockAggregate(mockedLags)

  "LagSensor.senseLag" should "include all neighbours and self when iterated" in:
    LagSensor.senseLag.toList shouldBe mockedLags.toList

  it should "exclude self when withoutSelf is used" in:
    LagSensor.senseLag.withoutSelf.toList shouldBe mockedLags.tail.toList
