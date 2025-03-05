package it.unibo.field4s.alchemist

trait AlchemistSensors:
  def sense[Value](name: String): Value
