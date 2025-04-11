package it.unibo.scafi.alchemist

trait AlchemistSensors:
  def sense[Value](name: String): Value
