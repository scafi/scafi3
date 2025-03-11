package it.unibo.field4s.alchemist.main

import it.unibo.field4s.language.AggregateFoundation
import it.unibo.field4s.language.libraries.All.{ *, given }
import it.unibo.field4s.language.sensors.DistanceSensor
import it.unibo.field4s.language.syntax.FieldCalculusSyntax

object TestProgram:
  type MyLanguage = AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax & DistanceSensor[Double]
  def myProgram(using MyLanguage): Int = evolve(0)(_ + 1)

  def myProgram2(using MyLanguage): Double =
    sensorDistanceTo(self == 0)
