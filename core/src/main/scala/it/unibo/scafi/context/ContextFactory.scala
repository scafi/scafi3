package it.unibo.scafi.context

import it.unibo.scafi.runtime.network.NetworkManager

trait ContextFactory[
    ID,
    Net <: NetworkManager { type DeviceId = ID },
    Context <: AggregateContext { type DeviceId = ID },
]:
  def createContext(deviceId: ID, network: Net): Context
