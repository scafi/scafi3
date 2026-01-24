package it.unibo.scafi.context.xc

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.context.common.BranchingContext
import it.unibo.scafi.language.xc.{ ExchangeLanguage, FieldBasedSharedData }
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.message.{ CodableFromTo, Import, InboundMessage, OutboundMessage, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.utils.AlignmentManager

/**
 * @tparam ID
 *   the type of the device identifier.
 */
trait ExchangeAggregateContext[ID](
    override val localId: ID,
    override val importFromInboundMessages: Import[ID],
    override val selfMessagesFromPreviousRound: ValueTree,
) extends AggregateContext,
      BranchingContext,
      ExchangeLanguage,
      ExchangeCalculus,
      FieldBasedSharedData,
      InboundMessage,
      OutboundMessage,
      AlignmentManager:
  override type DeviceId = ID

  override def xc[Format, Value: CodableFromTo[Format]](init: SharedData[Value])(
      f: SharedData[Value] => (SharedData[Value], SharedData[Value]),
  ): SharedData[Value] =
    alignmentScope("exchange"): () =>
      val messages = alignedMessages
      val field = Field(init(localId), alignedDevices.toSet, messages)
      val (ret, send) = f(field)
      writeValue(send.default, send.alignedValues)
      ret

  override def align[T](token: String)(body: () => T): T = alignmentScope(token)(body)
end ExchangeAggregateContext

object ExchangeAggregateContext:
  def exchangeContextFactory[ID, Network <: NetworkManager { type DeviceId = ID }](
      network: Network,
      selfMessagesFromPreviousRound: ValueTree,
  ): ExchangeAggregateContext[ID] =
    new ExchangeAggregateContext[ID](network.localId, network.receive, selfMessagesFromPreviousRound) {}
