package it.unibo.scafi.test

import it.unibo.scafi.alchemist.device.sensors.AlchemistEnvironmentVariables
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.libraries.All
import it.unibo.scafi.libraries.All.given
import it.unibo.scafi.libraries.FieldCalculusLibrary.share
import it.unibo.scafi.message.Codables.given
import it.unibo.scafi.sensors.DistanceSensor
import it.unibo.scafi.sensors.DistanceSensor.senseDistance

object Gradient:
  type Lang = AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax & DistanceSensor[Double] &
    AlchemistEnvironmentVariables

  def gradient(using Lang): Double =
    share(Double.MaxValue): prevValues =>
      val distances = senseDistance[Double]
      val minDistance = prevValues.alignedMap(distances)(_ + _).withoutSelf.min
      if AlchemistEnvironmentVariables.get[Boolean]("source") then 0.0 else minDistance
