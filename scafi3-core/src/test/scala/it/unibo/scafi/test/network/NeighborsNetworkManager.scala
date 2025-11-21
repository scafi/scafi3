package it.unibo.scafi.test.network

import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager

class NeighborsNetworkManager[ID](override val localId: ID, neighbors: Set[ID]) extends NetworkManager:
  override type DeviceId = ID

  override def receive: Import[ID] = Import(neighbors.map(id => id -> ValueTree.empty).toMap)

  override def send(message: Export[ID]): Unit = ()

  override def deliverableReceived(from: ID, message: ValueTree): Unit = ()
