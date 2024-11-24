package it.unibo.field4s.engine.context.exchange

import it.unibo.field4s.collections.ValueTree
import it.unibo.field4s.engine.context.Context
import it.unibo.field4s.engine.context.common.*
import it.unibo.field4s.engine.context.exchange.AbstractExchangeCalculusContext.ExportValue
import it.unibo.field4s.engine.network.Import
import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics

/**
 * Mixin composition of all the semantics needed to implement the exchange calculus, except for the message semantics.
 * @param self
 *   the device id of the current device
 * @param inboundMessages
 *   inbound messages as [[Import]]
 * @tparam Id
 *   the type of the device id
 * @tparam Wrapper
 *   the type of the envelope
 */
abstract class AbstractExchangeCalculusContext[Id, Wrapper](
    override val self: Id,
    override val inboundMessages: Import[Id, ExportValue[Wrapper]],
) extends Context[Id, ExportValue[Wrapper]]
    with ExchangeCalculusSemantics
    with NValuesSemantics
    with ConstructsSemantics
    with StackSemantics
    with MessageSemantics
    with InboundMessagesSemantics
    with OutboundMessagesSemantics:
  override type DeviceId = Id
  override type Envelope = Wrapper
end AbstractExchangeCalculusContext

object AbstractExchangeCalculusContext:
  type ExportValue[Wrapper] = ValueTree[InvocationCoordinate, Wrapper]
