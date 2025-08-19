package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{ ExecutionContext, Future }
import scala.jdk.CollectionConverters.*

import it.unibo.scafi.runtime.network.{ Neighborhood, NetworkManager }
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, Port }
import it.unibo.scafi.message.*
import it.unibo.scafi.message.Codable.*
import it.unibo.scafi.runtime.network.{ Neighborhood, NetworkManager }
import it.unibo.scafi.runtime.network.sockets.InetTypes.Endpoint
import it.unibo.scafi.utils.Channel

/**
 * A [[NetworkManager]] using plain platform sockets for communicating with neighbors.
 * @tparam ID
 *   the type of the self's device identifier.
 */
trait SocketNetworkManager[ID](deviceId: ID, port: Port)(using ExecutionContext)
    extends NetworkManager
    with ConnectionOrientedNetworking
    with AutoCloseable:
  self: InetAwareNeighborhoodResolver =>

  override type DeviceId = ID

  private type Envelope = (DeviceId, ValueTree)

  override type MessageIn = Envelope

  override type MessageOut = Envelope

  private val outChannel = Channel[Set[MessageOut]]
  private val inValues = ConcurrentHashMap[DeviceId, ValueTree]()
  private val connectionsListener = AtomicReference[ListenerRef]()

  def start(): Future[Unit] =
    for
      _ <- if outChannel.isClosed then Future.failed(IllegalStateException("Network manager's closed")) else Future.unit
      listenerRef <- server(port)
      _ = connectionsListener.set(listenerRef)
      _ = client(Map.empty)
    yield ()

  override def send(message: Export[DeviceId]): Unit =
    try outChannel.push((neighborhood - deviceId).map(id => id -> message(id)))
    catch case _: Channel.ChannelClosedException => scribe.error("The network manager is closed, cannot send message.")

  override def receive: Import[DeviceId] = Import(inValues.asScala.toMap)

  private def client(connections: Map[Endpoint, Connection]): Future[Unit] =
    for
      msgs <- outChannel.take
      nvalues = msgs.flatMap((nid, valueTree) => nid.endpoint.map(_ -> valueTree))
      newConnections <- Future.traverse(nvalues): (endpoint, valueTree) =>
        connections
          .get(endpoint)
          .filter(_.isOpen)
          .fold(establishConnection(endpoint))(Future.successful)
          .flatMap(conn => conn.send((deviceId, valueTree)).map(_ => Right(endpoint -> conn)))
          .recover { case e => Left(e) }
      _ <- client(newConnections.collect { case Right(nc) => nc }.toMap)
    yield ()

  private def establishConnection(endpoint: Endpoint): Future[Connection] = out(endpoint)

  private def server(port: Port): Future[ListenerRef] = in(port)(inValues.put(_, _): Unit)

  override def close(): Unit = try
    outChannel.close()
    Option(connectionsListener.get()).foreach(_.listener.close())
  catch case e: (Channel.ChannelClosedException | Exception) => scribe.error(e)

  def boundPort: Option[Port] = Option(connectionsListener.get()).map(_.listener.boundPort)

end SocketNetworkManager

object SocketNetworkManager:
  import it.unibo.scafi.runtime.network.CodableInstances.given

  /**
   * Creates a new socket-based [[NetworkManager]] with statically assigned neighbors.
   * @param deviceId
   *   the device identifier of the self-node.
   * @param port
   *   the port to listen for incoming connections.
   * @param neighbors
   *   the map of neighbors with their device identifiers and endpoints.
   * @tparam ID
   *   the type of the device identifier.
   * @return
   *   a newly created, not yet started instance of [[SocketNetworkManager]].
   * @see
   *   [[SocketNetworkManager.start]] to start the network manager.
   */
  def withFixedNeighbors[ID: BinaryCodable](
      deviceId: ID,
      port: Port,
      neighbors: => Map[ID, Endpoint],
  )(using ExecutionContext, SocketConfiguration): SocketNetworkManager[ID] =
    new SocketNetworkManager(deviceId, port) with SocketNetworking with InetAwareNeighborhoodResolver:
      override def neighborhood: Neighborhood[DeviceId] = neighbors.keySet
      extension (id: ID) override def endpoint: Option[Endpoint] = neighbors.get(id)
end SocketNetworkManager
