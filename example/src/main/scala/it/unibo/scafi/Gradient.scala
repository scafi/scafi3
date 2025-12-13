package it.unibo.scafi

import it.unibo.alchemist.boundary.LoadAlchemist
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
  private type Lang = AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax & DistanceSensor[Double] &
    AlchemistEnvironmentVariables

  def gradient(using Lang): Double =
    share(Double.MaxValue): prevValues =>
      val distances = senseDistance[Double]
      val minDistance = (prevValues, distances).mapN(_ + _).withoutSelf.min
      if AlchemistEnvironmentVariables.get[Boolean]("source") then 0.0 else minDistance

  @main
  def run(): Unit =
    val simulationFile = getClass.getResource("/it/unibo/scafi/gradient.yml").getPath
    val loader = LoadAlchemist.from(simulationFile)
    val simulation = loader.getDefault[Any, Nothing]()
    simulation.run()
