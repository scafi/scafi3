package it.unibo.scafi.utils.network

import it.unibo.scafi.message.{ Export, Import }
import it.unibo.scafi.runtime.network.NetworkManager

/**
 * This class is meant to be used when no neighbors are present in the network. It is used to test the behavior of the
 * program itself.
 * @tparam Id
 *   the type of the deviceId of neighbor devices.
 */
class NoNeighborsNetworkManager[Id] extends NetworkManager:
  override type DeviceId = Id

  override def receive: Import[Id] = Import.empty

  override def send(message: Export[Id]): Unit = ()
