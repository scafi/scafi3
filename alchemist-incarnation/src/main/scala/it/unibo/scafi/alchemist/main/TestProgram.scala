package it.unibo.scafi.alchemist.main

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.libraries.All.{ *, given }
import it.unibo.scafi.language.sensors.DistanceSensor
import it.unibo.scafi.language.syntax.FieldCalculusSyntax

object TestProgram:
  type MyLanguage = AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax & DistanceSensor[Double]
  def myProgram(using MyLanguage): Int = evolve(0)(_ + 1)

  def myProgram2(using MyLanguage): Double =
    sensorDistanceTo(self == 0)
