package it.unibo.field4s.engine.context

import it.unibo.field4s.engine.network.Network

/**
 * A context factory is able to create a context from a given network.
 * @tparam N
 *   the network type
 * @tparam C
 *   the context type
 */
trait ContextFactory[-N <: Network[?, ?], +C <: Context[?, ?]]:
  /**
   * Creates a context from a given [[Network]], that provides the local id and inbound messages.
   * @param network
   *   the network
   * @return
   *   the context
   */
  def create(network: N): C
