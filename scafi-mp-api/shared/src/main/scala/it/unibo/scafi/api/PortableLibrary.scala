package it.unibo.scafi.api

/**
 * The root base trait for all portable library components.
 */
trait PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation

  type PortableDeviceId

  type PortableSharedData[Value]
