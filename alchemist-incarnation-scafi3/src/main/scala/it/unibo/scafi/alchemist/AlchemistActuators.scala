package it.unibo.scafi.alchemist

trait AlchemistActuators:
  def update[Value](name: String, value: Value): Unit
