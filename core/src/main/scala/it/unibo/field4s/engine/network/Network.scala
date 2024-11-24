package it.unibo.field4s.engine.network

/**
 * A network is a communication channel between devices. It is used to send and receive messages in form of [[Export]].
 * Additionally, it provides ids for the local device and could implement network based sensors used to create a
 * [[it.unibo.scafi.xc.engine.context.Context Context]].
 * @tparam DeviceId
 *   the type of the device id
 * @tparam Value
 *   the type of the values in the tree
 */
trait Network[DeviceId, Value]:

  /**
   * @return
   *   the id of the local device
   */
  def localId: DeviceId

  /**
   * Sends messages to neighbours in the network.
   * @param e
   *   the messages to send
   */
  def send(e: Export[DeviceId, Value]): Unit

  /**
   * Captures the last versions of values received from neighbours in the network. In the aggregate semantics of
   * networks, they must discard stale values, according to some expiration policy.
   * @return
   *   the messages received from neighbours in the network
   */
  def receive(): Import[DeviceId, Value]
end Network
