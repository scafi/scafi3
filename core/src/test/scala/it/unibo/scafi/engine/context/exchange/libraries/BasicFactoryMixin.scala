package it.unibo.scafi.engine.context.exchange.libraries

import it.unibo.scafi.engine.context.ValueTreeTestingNetwork
import it.unibo.scafi.collections.ValueTree
import it.unibo.scafi.engine.context.ContextFactory
import it.unibo.scafi.engine.context.common.InvocationCoordinate
import it.unibo.scafi.engine.context.exchange.BasicExchangeCalculusContext

trait BasicFactoryMixin:
  type ExportValue = BasicExchangeCalculusContext.ExportValue

  val factory
      : ContextFactory[ValueTreeTestingNetwork[Int, InvocationCoordinate, Any], BasicExchangeCalculusContext[Int]] =
    n => new BasicExchangeCalculusContext[Int](n.localId, n.received)
