package it.unibo.scafi.context

import it.unibo.scafi.runtime.network.Network

trait ContextFactory[DeviceId, Net <: Network[DeviceId], Ctx <: AggregateContext[DeviceId]]:
  def createContext(network: Net): Ctx
