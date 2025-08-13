package it.unibo.scafi.utils

enum PlatformRuntime:
  case Jvm
  case Js
  case Native

object PlatformRuntime:

  given CanEqual[PlatformRuntime, PlatformRuntime] = CanEqual.derived
