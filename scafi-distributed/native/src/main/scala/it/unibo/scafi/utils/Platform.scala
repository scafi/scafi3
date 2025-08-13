package it.unibo.scafi.utils

/**
 * Native platform-specific utilities.
 */
object Platform extends BlockingPlatform:

  def runtime: PlatformRuntime = PlatformRuntime.Native
