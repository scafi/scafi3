package it.unibo.field4s.alchemist

trait AlchemistActuators:
  def update[Value](name: String, value: Value): Unit
