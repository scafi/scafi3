package it.unibo.scafi.api

import scala.scalajs.js.annotation.JSExportAll

trait PortableXCApi:
  import it.unibo.scafi.*

  @JSExportAll
  trait ADTs extends PortableTypes {}

  @JSExportAll
  trait Interface extends PortableCommonLibrary with PortableExchangeCalculusLibrary:
    ctx: ADTs =>

    override type Language = AggregateFoundation { type DeviceId = PortableDeviceId } &
      language.xc.syntax.ExchangeSyntax & language.xc.FieldBasedSharedData

    override type PortableDeviceId = Int
    override given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = Iso.id
