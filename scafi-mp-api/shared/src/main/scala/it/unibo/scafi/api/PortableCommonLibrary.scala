package it.unibo.scafi.api

trait PortableCommonLibrary extends PortableLibrary:
  ctx: PortableTypes =>

  @JSExport
  def localId(using language: Language): PortableDeviceId = language.localId
