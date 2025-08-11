package it.unibo.scafi.test

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.{ Codable, Codables, Export }
import it.unibo.scafi.runtime.network.NetworkManager

trait AggregateProgramProbe:

  given [Message]: Codable[Message, Message] = Codables.forInMemoryCommunications[Message]

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
