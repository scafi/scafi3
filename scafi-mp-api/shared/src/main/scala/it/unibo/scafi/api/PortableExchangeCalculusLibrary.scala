package it.unibo.scafi.api

import scala.util.chaining.scalaUtilChainingOps

/**
 * The portable library providing the exchange calculus primitive, `exchange`.
 */
trait PortableExchangeCalculusLibrary extends PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }

  override type Language <: AggregateFoundation & ExchangeSyntax:
    type DeviceId = PortableDeviceId

  @JSExport
  def exchange[T](initial: PortableSharedData[T])(
      f: Function1[PortableSharedData[T], Tuple2[PortableSharedData[T], PortableSharedData[T]]],
  ): PortableSharedData[T] = language.exchange(initial)(f(_).pipe(f => ReturnSending(f._1, f._2)))
