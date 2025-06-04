package it.unibo.scafi.api

/**
 * The XC entry point language- and platform-agnostic library.
 */
trait PortableXCApi extends PortableApi:
  import it.unibo.scafi.language.xc

  @JSExportAll
  trait ADTs extends PortableTypes {}

  @JSExportAll
  trait Interface extends PortableCommonLibrary with PortableExchangeCalculusLibrary:
    ctx: ADTs =>

    override type Language =
      AggregateFoundation { type DeviceId = PortableDeviceId } & xc.syntax.ExchangeSyntax & xc.FieldBasedSharedData

    override type PortableDeviceId = Int
    override given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = Iso.id
