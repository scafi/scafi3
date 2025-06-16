package it.unibo.scafi.api

import scala.util.chaining.scalaUtilChainingOps

/**
 * The portable library providing the exchange calculus primitive, `exchange`.
 */
trait PortableExchangeCalculusLibrary extends PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.xc.syntax.ExchangeSyntax
  import it.unibo.scafi.language.xc.syntax.ReturnSending as RetSend

  override type Language <: AggregateFoundation & ExchangeSyntax

  @JSExport
  def exchange[T](initial: PortableSharedData[T])(
      f: Function1[PortableSharedData[T], ReturnSending[PortableSharedData[T]]],
  ): PortableSharedData[T] = language.exchange(initial)(f(_).pipe(f => RetSend(f.returning, f.sending)))
