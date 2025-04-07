package it.unibo.scafi.engine.context.exchange

import it.unibo.scafi.engine.context.common.*
import it.unibo.scafi.engine.context.exchange.BasicExchangeCalculusContext.ExportValue
import it.unibo.scafi.engine.network.Import

/**
 * Implements a basic version of an exchange calculus context that wraps any value into [[Any]].
 * @param localId
 *   the device id of the current device
 * @param inboundMessages
 *   inbound messages as [[Import]]
 * @tparam Id
 *   the type of the device id
 * @see
 *   [[AbstractExchangeCalculusContext]]
 */
class BasicExchangeCalculusContext[Id](
    override val localId: Id,
    inboundMessages: Import[Id, ExportValue],
) extends AbstractExchangeCalculusContext[Id, Any](localId, inboundMessages)
    with MessageManager.Basic

object BasicExchangeCalculusContext:
  type ExportValue = AbstractExchangeCalculusContext.ExportValue[Any]
