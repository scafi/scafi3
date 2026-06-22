package it.unibo.scafi.language.sensors

import scala.concurrent.duration.*

import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.sensors.TimeSensor

class TimeSensorTests extends UnitTest:

  given CanEqual[FiniteDuration, FiniteDuration] = CanEqual.derived

  type Language = AggregateFoundation & TimeSensor

  given lang: Language = new AggregateFoundationMock with TimeSensor:
    override def deltaTime: FiniteDuration = 100.millis
    override def timestamp: Long = 42L

  "TimeSensor.deltaTime" should "delegate to the foundation" in:
    TimeSensor.deltaTime shouldBe lang.deltaTime

  "TimeSensor.timestamp" should "delegate to the foundation" in:
    TimeSensor.timestamp shouldBe lang.timestamp

  "TimeSensor.deltaTime" should "return the expected FiniteDuration" in:
    TimeSensor.deltaTime shouldBe 100.millis

  "TimeSensor.timestamp" should "return the expected Long" in:
    TimeSensor.timestamp shouldBe 42L
