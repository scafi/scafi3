package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

import it.unibo.scafi.message.{ BinaryCodable, BinaryDecodable, BinaryEncodable }
import it.unibo.scafi.runtime.network.{ ExpirationConfiguration, Neighborhood, TimeRetention }
import it.unibo.scafi.runtime.network.CodableInstances.given
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, Port }

object SocketNetworkManager:

  /**
   * Creates a new socket-based [[NetworkManager]] with statically assigned neighbors and configurable [[TimeRetention]]
   * expiration policy.
   * @param deviceId
   *   the device identifier of the self-node.
   * @param port
   *   the port to listen for incoming connections.
   * @param neighbors
   *   the map of neighbors with their device identifiers and endpoints.
   * @tparam ID
   *   the type of the device identifier.
   * @return
   *   a newly created, not yet started instance of [[ConnectionOrientedNetworkManager]].
   * @see
   *   [[ConnectionOrientedNetworkManager.start]] to start the network manager.
   */
  def withFixedNeighbors[ID: BinaryCodable](
      deviceId: ID,
      port: Port,
      neighbors: => Map[ID, Endpoint],
  )(using ExecutionContext, ConnectionConfiguration, ExpirationConfiguration): ConnectionOrientedNetworkManager[ID] =
    new ConnectionOrientedNetworkManager(deviceId, port)
      with TimeRetention
      with SocketNetworking
      with InetAwareNeighborhoodResolver:
      override def neighborhood: Neighborhood[DeviceId] = neighbors.keySet
      extension (id: ID) override def endpoint: Option[Endpoint] = neighbors.get(id)
end SocketNetworkManager
