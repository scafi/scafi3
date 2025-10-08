package it.unibo.scafi.runtime.network.sockets

import it.unibo.scafi.runtime.network.NeighborhoodResolver
import it.unibo.scafi.runtime.network.sockets.InetTypes.Endpoint

/**
 * A [[NeighborhoodResolver]] capable of resolving device identifiers to network endpoints.
 */
trait InetAwareNeighborhoodResolver extends NeighborhoodResolver:

  extension (id: DeviceId)

    /** @return the endpoint associated with the device identifier, if available. */
    def endpoint: Option[Endpoint]
