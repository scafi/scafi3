package it.unibo.scafi.alchemist.device.actuators

trait AlchemistActuators:
  def update[Value](name: String, value: Value): Unit
