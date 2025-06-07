package it.unibo.scafi.api

trait PortableCommonLibrary extends PortableLibrary:
  ctx: PortableTypes =>

  @JSExport
  def localId(using language: Language): PortableDeviceId = language.localId

  @JSExport
  def device(using language: Language): PortableSharedData[language.DeviceId] = language.device
