package it.unibo.scafi.runtime.network.sockets

import it.unibo.scafi.runtime.network.NeighborhoodResolver
import it.unibo.scafi.runtime.network.sockets.InetTypes.Endpoint

trait InetAwareNeighborhoodResolver extends NeighborhoodResolver:

  extension (id: DeviceId) def reachableAt: Option[Endpoint]
