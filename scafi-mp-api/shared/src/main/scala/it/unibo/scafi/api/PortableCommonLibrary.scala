package it.unibo.scafi.api

/**
 * The portable library providing common utility functions that are often used in programs and libraries.
 */
trait PortableCommonLibrary extends PortableLibrary:
  ctx: PortableTypes =>

  @JSExport
  def localId(using language: AggregateFoundation { type DeviceId = PortableDeviceId }): PortableDeviceId =
    language.localId
