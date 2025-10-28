package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ Export, Import, ValueTree }

/**
 * NetworkManager is a trait that defines the interface for managing network communication. It provides methods to send
 * and receive messages.
 */
trait NetworkManager:
  /**
   * The type of the deviceId of neighbor devices.
   */
  type DeviceId

  /**
   * @return
   *   the device identifier of the current device
   */
  def localId: DeviceId

  /**
   * Sends the [[message]] to neighbor devices.
   * @param message
   *   the [[Export]] to send to neighbor devices.
   */
  def send(message: Export[DeviceId]): Unit

  /**
   * Receives the neighbors' [[Import]] from the network.
   * @return
   *   the [[Import]] received from the network.
   */
  def receive: Import[DeviceId]

  def deliverableReceived(from: DeviceId, message: ValueTree): Unit
end NetworkManager
