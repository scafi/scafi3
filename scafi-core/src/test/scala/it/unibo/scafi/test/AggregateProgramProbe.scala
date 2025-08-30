package it.unibo.scafi.test

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.Export
import it.unibo.scafi.runtime.network.NetworkManager

trait AggregateProgramProbe:
  def roundForAggregateProgram[
      ID,
      Result,
      Context <: AggregateContext { type DeviceId = ID },
  ](
      localId: ID,
      network: NetworkManager { type DeviceId = ID },
      factory: (ID, NetworkManager { type DeviceId = ID }) => Context,
  )(
      aggregateProgram: Context ?=> Result,
  ): (Result, Export[ID]) =
    val context: Context = factory(localId, network)
    val result = aggregateProgram(using context)
    (result, context.exportFromOutboundMessages)
