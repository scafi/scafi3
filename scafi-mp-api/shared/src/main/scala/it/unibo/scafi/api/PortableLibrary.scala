package it.unibo.scafi.api

trait PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation

  type Language <: AggregateFoundation { type DeviceId = PortableDeviceId }

  type PortableDeviceId
  given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = compiletime.deferred

  type PortableSharedData[Value]
  given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] = compiletime.deferred
