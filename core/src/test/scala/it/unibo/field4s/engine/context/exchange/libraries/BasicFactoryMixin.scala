package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.engine.context.ValueTreeTestingNetwork
import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.engine.context.ContextFactory
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext

trait BasicFactoryMixin:
  type ExportValue = BasicExchangeCalculusContext.ExportValue

  val factory
      : ContextFactory[ValueTreeTestingNetwork[Int, InvocationCoordinate, Any], BasicExchangeCalculusContext[Int]] =
    n => new BasicExchangeCalculusContext[Int](n.localId, n.received)
