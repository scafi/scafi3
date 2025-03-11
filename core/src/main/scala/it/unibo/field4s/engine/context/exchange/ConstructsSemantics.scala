package it.unibo.field4s.engine.context.exchange

import it.unibo.field4s.engine.context.common.*
import it.unibo.field4s.language.exchange.semantics.ExchangeCalculusSemantics

/**
 * Implements the foundational constructs of the exchange calculus semantics.
 */
trait ConstructsSemantics:
  this: ExchangeCalculusSemantics & NeighboringValueSemantics & Message & Stack & InboundMessages & OutboundMessage =>

  override protected def br[T](cond: Boolean)(th: => T)(el: => T): T =
    scope(s"branch/$cond"): () =>
      if cond then th else el

  override protected def xc[T](init: SharedData[T])(
      f: SharedData[T] => (SharedData[T], SharedData[T]),
  ): SharedData[T] =
    scope("exchange"): () =>
      val messages = alignedMessages.map((k, v) => (k, open[T](v)))
      val subject = NValues[T](init(self), messages)
      val (ret, send) = f(subject)
      sendMessages(send.alignedValues, send.default)
      ret

end ConstructsSemantics
