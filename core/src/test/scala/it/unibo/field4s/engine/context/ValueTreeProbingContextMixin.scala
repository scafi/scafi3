package it.unibo.field4s.engine.context

import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.engine.network.{ Export, Import }

trait ValueTreeProbingContextMixin:

  def probe[Id, Tokens, Values, C <: Context[Id, ValueTree[Tokens, Values]]](
      localId: Id,
      factory: ContextFactory[ValueTreeTestingNetwork[Id, Tokens, Values], C],
      program: C ?=> Any,
      inboundMessages: Import[Id, ValueTree[Tokens, Values]],
  ): Export[Id, ValueTree[Tokens, Values]] =
    val network: ValueTreeTestingNetwork[Id, Tokens, Values] = ValueTreeTestingNetwork(
      localId = localId,
      received = inboundMessages,
    )
    val probingContext: C = factory.create(network)
    program(using probingContext)
    network.send(probingContext.outboundMessages)
    network.sent

  def probe[Id, Tokens, Values, C <: Context[Id, ValueTree[Tokens, Values]]](
      localId: Id,
      factory: ContextFactory[ValueTreeTestingNetwork[Id, Tokens, Values], C],
      program: C ?=> Any,
  ): Export[Id, ValueTree[Tokens, Values]] =
    probe(localId, factory, program, Map.empty)
end ValueTreeProbingContextMixin
