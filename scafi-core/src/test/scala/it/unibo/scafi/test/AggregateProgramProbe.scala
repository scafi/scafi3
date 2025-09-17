package it.unibo.scafi.test

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.{ Codable, Codables, Export, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager

trait AggregateProgramProbe:

  given valuesCodable[Value]: Codable[Value, Value] = Codables.forInMemoryCommunications[Value]

  def roundForAggregateProgram[
      ID,
      Result,
      Context <: AggregateContext { type DeviceId = ID },
  ](
      localId: ID,
      network: NetworkManager { type DeviceId = ID },
      factory: (ID, NetworkManager { type DeviceId = ID }, ValueTree) => Context,
  )(
      aggregateProgram: Context ?=> Result,
  ): (Result, Export[ID]) =
    val engine = ScafiEngine(localId, network, factory)(aggregateProgram)
    (engine.cycle(), engine.lastExportResult)
