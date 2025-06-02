package it.unibo.scafi.api

import scala.util.chaining.scalaUtilChainingOps

trait PortableExchangeCalculusLibrary extends PortableCommonLibrary:
  ctx: PortableTypes =>

  import it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }

  override type Language <: AggregateFoundation { type DeviceId = PortableDeviceId } & ExchangeSyntax

  @JSExport
  def exchange[T](using
      language: Language,
  )(initial: PortableSharedData[T])(
      f: Function1[PortableSharedData[T], Tuple2[PortableSharedData[T], PortableSharedData[T]]],
  ): PortableSharedData[T] = language.exchange(initial)(f(_).pipe(f => ReturnSending(f._1, f._2)))
