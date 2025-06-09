package it.unibo.scafi.api

/**
 * The XC entry point language- and platform-agnostic library.
 */
trait PortableXCLibrary
    extends PortableCommonLibrary
    with PortableBranchingLibrary
    with PortableExchangeCalculusLibrary:
  ctx: PortableTypes =>
  override type PortableDeviceId = Int
