package it.unibo.scafi.utils

/**
 * JVM platform-specific utilities.
 */
object Platform extends BlockingPlatform:

  def runtime: PlatformRuntime = PlatformRuntime.Jvm
