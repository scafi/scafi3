package it.unibo.field4s.engine.context.exchange

import it.unibo.field4s.engine.context.common.*
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext.ExportValue
import it.unibo.field4s.engine.network.Import

/**
 * Implements a basic version of an exchange calculus context that wraps any value into [[Any]].
 * @param self
 *   the device id of the current device
 * @param inboundMessages
 *   inbound messages as [[Import]]
 * @tparam Id
 *   the type of the device id
 * @see
 *   [[AbstractExchangeCalculusContext]]
 */
class BasicExchangeCalculusContext[Id](
    self: Id,
    inboundMessages: Import[Id, ExportValue],
) extends AbstractExchangeCalculusContext[Id, Any](self, inboundMessages)
    with MessageSemantics.Basic

object BasicExchangeCalculusContext:
  type ExportValue = AbstractExchangeCalculusContext.ExportValue[Any]
