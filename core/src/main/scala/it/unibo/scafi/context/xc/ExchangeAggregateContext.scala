package it.unibo.scafi.context.xc

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.language.xc.{ ExchangeLanguage, FieldBasedSharedData }
import it.unibo.scafi.message.{ Import, InboundMessage, OutboundMessage }
import it.unibo.scafi.utils.Stack

trait ExchangeAggregateContext[ID](
    override val localId: ID,
    override val importFromInboundMessages: Import[ID],
) extends AggregateContext,
      ExchangeLanguage,
      ExchangeCalculus,
      FieldBasedSharedData,
      InboundMessage,
      OutboundMessage,
      Stack:
  override type DeviceId = ID

  override def xc[T](init: SharedData[T])(f: SharedData[T] => (SharedData[T], SharedData[T])): SharedData[T] =
    alignmentScope("exchange"): () =>
      val messages = alignedMessages[T].map { case (id, value) => id -> value }
      val field = Field(init(localId), messages)
      val (ret, send) = f(field)
      writeValue(send.default, send.alignedValues)
      ret

  override def br[T](cond: Boolean)(th: => T)(el: => T): T = alignmentScope(s"branch/$cond"): () =>
    if cond then th else el

  override def align[T](token: Any)(body: () => T): T = alignmentScope(token.toString)(body)
end ExchangeAggregateContext
