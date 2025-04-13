package it.unibo.scafi.utils

import it.unibo.scafi.context.{ AggregateContext, ContextFactory }
import it.unibo.scafi.message.Export

trait AggregateProgramProbe:
  def roundForAggregateProgram[Id, Result, Context <: AggregateContext { type DeviceId = Id }](
      localId: Id,
      factory: ContextFactory[Id, NoNeighborsNetworkManager[Id], Context],
  )(
      aggregateProgram: Context ?=> Result,
  ): (Result, Export[Id]) =
    val context = factory(localId, NoNeighborsNetworkManager[Id]())
    val result = aggregateProgram(using context)
    (result, context.exportFromOutboundMessages)
