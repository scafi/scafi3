package it.unibo.field4s.engine.context

import it.unibo.field4s.engine.network.{ Export, Import }

/**
 * A context implements all the logic to handle value tree manipulation and message exchange from a device to itself and
 * its neighbours. Ideally, a context is created from inbound messages and outputs outbound messages as a result of some
 * computation.
 * @tparam DeviceId
 *   the type of the device identifiers
 * @tparam Value
 *   the type that wraps all the values exchanged between devices
 */
trait Context[DeviceId, Value]:

  /**
   * @return
   *   the [[Import]] that contains all the inbound value trees from self and neighbours.
   */
  def inboundMessages: Import[DeviceId, Value]

  /**
   * @return
   *   the [[Export]] that contains all the outbound value trees to self and neighbours, and a default value tree for
   *   new neighbours.
   */
  def outboundMessages: Export[DeviceId, Value]
end Context
