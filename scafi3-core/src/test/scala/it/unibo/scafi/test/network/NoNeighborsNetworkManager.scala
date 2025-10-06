package it.unibo.scafi.test.network

import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager

/**
 * This class is meant to be used when no neighbors are present in the network. It is used to test the behavior of the
 * program itself.
 * @tparam ID
 *   the type of the deviceId of neighbor devices.
 */
class NoNeighborsNetworkManager[ID] extends NetworkManager:
  override type DeviceId = ID

  override def receive: Import[ID] = Import.empty

  override def send(message: Export[ID]): Unit = ()

  override def deliverableReceived(from: ID, message: ValueTree): Unit = ()
