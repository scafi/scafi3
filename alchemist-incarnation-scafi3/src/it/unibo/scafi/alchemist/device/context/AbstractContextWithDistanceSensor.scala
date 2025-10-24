package it.unibo.scafi.alchemist.device.context

import it.unibo.alchemist.model.Position
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.sensors.DistanceSensor

trait AbstractContextWithDistanceSensor[P <: Position[P]] extends DistanceSensor[Double]:
  self: AggregateFoundation =>
