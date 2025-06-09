package it.unibo.scafi.api

import scala.util.chaining.scalaUtilChainingOps

/**
 * The portable library providing the exchange calculus primitive, `exchange`.
 */
trait PortableExchangeCalculusLibrary extends PortableLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language.xc.FieldBasedSharedData
  import it.unibo.scafi.language.xc.syntax.{ ExchangeSyntax, ReturnSending }

  type Language = AggregateFoundation { type DeviceId = PortableDeviceId } & ExchangeSyntax & FieldBasedSharedData

  @JSExport
  def exchange[T](using
      language: Language,
  )(initial: PortableSharedData[T])(
      f: Function1[PortableSharedData[T], Tuple2[PortableSharedData[T], PortableSharedData[T]]],
  ): PortableSharedData[T] = language.exchange(initial)(f(_).pipe(f => ReturnSending(f._1, f._2)))

  given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] = compiletime.deferred
