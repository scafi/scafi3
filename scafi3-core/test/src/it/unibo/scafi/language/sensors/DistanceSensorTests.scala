package it.unibo.scafi.language.sensors

object DistanceSensorTests

//package it.unibo.scafi.language.sensors
//
//import it.unibo.scafi.UnitTest
//import it.unibo.scafi.language.foundation.AggregateFoundationMock
//import it.unibo.scafi.language.AggregateFoundation
//import it.unibo.scafi.sensors.DistanceSensor
//import it.unibo.scafi.sensors.DistanceSensor.senseDistance
//
//import cats.syntax.all.*
//
//class DistanceSensorTests extends UnitTest:
//
//  type Language = AggregateFoundation & DistanceSensor[Int]
//
//  given lang: Language = new AggregateFoundationMock with DistanceSensor[Int]:
//    override def senseDistance: MockAggregate[Int] = device.map(_ => 1)
//
//  "senseDistance" should "be available from language" in:
//    "val _: lang.SharedData[Int] = lang.senseDistance" should compile
//    "val _: lang.SharedData[Double] = lang.senseDistance" shouldNot typeCheck
//
//  it should "be callable statically" in:
//    senseDistance[Int] should equal(lang.senseDistance)
