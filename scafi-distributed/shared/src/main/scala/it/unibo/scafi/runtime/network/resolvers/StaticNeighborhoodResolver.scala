package it.unibo.scafi.runtime.network.resolvers

import it.unibo.scafi.runtime.network.NeighborhoodResolver
import it.unibo.scafi.runtime.network.Neighborhood

trait StaticNeighborhoodResolver[DeviceId](neighbors: Set[DeviceId]) extends NeighborhoodResolver[DeviceId]:
  override def apply(): Neighborhood[DeviceId] = Neighborhood(neighbors)
