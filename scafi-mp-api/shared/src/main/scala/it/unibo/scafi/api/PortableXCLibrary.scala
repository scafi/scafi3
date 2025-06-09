package it.unibo.scafi.api

/**
 * The XC entry point language- and platform-agnostic library.
 */
trait PortableXCLibrary
    extends PortableCommonLibrary
    with PortableExchangeCalculusLibrary
    with PortableBranchingLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language

  override type Language = AggregateFoundation { type DeviceId = PortableDeviceId } &
    language.xc.syntax.ExchangeSyntax & language.xc.FieldBasedSharedData & language.common.syntax.BranchingSyntax

  override type PortableDeviceId = Int
  override given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = Iso.id
