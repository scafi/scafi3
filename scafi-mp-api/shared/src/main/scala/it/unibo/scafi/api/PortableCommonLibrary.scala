package it.unibo.scafi.api

trait PortableCommonLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation

  type PortableDeviceId
  given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = compiletime.deferred

  type PortableSharedData[Value]
  given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] = compiletime.deferred

  type Language <: AggregateFoundation { type DeviceId = PortableDeviceId }

  @JSExport
  def localId(using language: Language): PortableDeviceId = language.localId
end PortableCommonLibrary
