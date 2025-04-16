package it.unibo.scafi.context

import it.unibo.scafi.runtime.network.NetworkManager

trait ContextFactory[
    ID,
    Network <: NetworkManager { type DeviceId = ID },
    Context <: AggregateContext { type DeviceId = ID },
] extends ((ID, Network) => Context)
