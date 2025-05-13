package it.unibo.scafi.engine.context.exchange

import it.unibo.scafi.engine.context.common.*
import it.unibo.scafi.engine.path.Path
import it.unibo.scafi.language.exchange.FieldBasedSharedData
import it.unibo.scafi.language.exchange.semantics.ExchangeCalculusSemantics

/**
 * Implements the foundational constructs of the exchange calculus semantics.
 */
trait ConstructsSemantics:
  this: ExchangeCalculusSemantics & FieldBasedSharedData & MessageManager & Stack & InboundMessages & OutboundMessage =>

  override protected def br[T](cond: Boolean)(th: => T)(el: => T): T =
    scope(s"branch/$cond"): () =>
      if cond then th else el

  override protected def xc[T](init: SharedData[T])(f: SharedData[T] => (SharedData[T], SharedData[T])): SharedData[T] =
    scope("exchange"): () =>
      val messages = alignedMessages.map((k, v) => (k, open[T](v)))
      val subject = Field[T](init(localId), messages)
      val (ret, send) = f(subject)
      sendMessages(send.alignedValues.view, send.default)
      ret

  override def align[T](path: Any)(body: () => T): T =
    scope(path.mkString("/"))(body)

end ConstructsSemantics
