package it.unibo.scafi.utils

sealed trait RuntimePlatform

object RuntimePlatform:
  case object Jvm extends RuntimePlatform

  case object Native extends RuntimePlatform

  case object Js extends RuntimePlatform

trait PlatformInfo:
  export it.unibo.scafi.utils.RuntimePlatform.*

  def platform: RuntimePlatform
