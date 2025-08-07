package it.unibo.scafi.runtime.network.sockets

import it.unibo.scafi.runtime.network.NeighborhoodResolver
import it.unibo.scafi.runtime.network.Neighborhood

trait InetAwareNeighborhoodResolver extends NeighborhoodResolver:
  import InetTypes.*

  extension (id: DeviceId) def reachableAt: Option[Endpoint]

  def reachableNeighbors: Neighborhood[Endpoint] = resolve().map(_.reachableAt).collect { case Some(n) => n }
