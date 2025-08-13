package it.unibo.scafi.utils

/**
 * Represents any supported platform runtime.
 */
enum PlatformRuntime:
  case Jvm
  case Js
  case Native

object PlatformRuntime:

  given CanEqual[PlatformRuntime, PlatformRuntime] = CanEqual.derived
