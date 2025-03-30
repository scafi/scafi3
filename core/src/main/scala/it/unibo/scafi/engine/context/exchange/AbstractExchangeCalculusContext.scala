package it.unibo.scafi.engine.context.exchange

import it.unibo.scafi.collections.ValueTree
import it.unibo.scafi.engine.context.Context
import it.unibo.scafi.engine.context.common.*
import it.unibo.scafi.engine.context.exchange.AbstractExchangeCalculusContext.ExportValue
import it.unibo.scafi.engine.network.Import
import it.unibo.scafi.language.exchange.{ ExchangeLanguage, FieldBasedSharedData }
import it.unibo.scafi.language.exchange.semantics.ExchangeCalculusSemantics

/**
 * Mixin composition of all the semantics needed to implement the exchange calculus, except for the message semantics.
 *
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
) extends Context[Id, ExportValue[Wrapper]],
      ExchangeCalculusSemantics,
      FieldBasedSharedData,
      ConstructsSemantics,
      Stack,
      MessageManager,
      InboundMessages,
      OutboundMessage,
      ExchangeLanguage:
  override type DeviceId = Id
  override type Envelope = Wrapper

object AbstractExchangeCalculusContext:
  type ExportValue[Wrapper] = ValueTree[InvocationCoordinate, Wrapper]
