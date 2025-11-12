package it.unibo.scafi.runtime

import scala.scalanative.unsafe.Zone

import it.unibo.scafi.types.MemorySafeContext

/**
 * A memory-safe context implementation for native platforms using `Zone` for scoped memory management.
 */
trait NativeMemoryContext extends MemorySafeContext:

  override type Arena = Zone

  inline override def safelyRun[T](block: Zone ?=> T): T = Zone(block)
