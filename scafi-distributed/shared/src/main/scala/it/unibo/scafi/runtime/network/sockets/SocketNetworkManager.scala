package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ConcurrentHashMap

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
  private var connectionsListener: Option[ListenerRef] = None

  def start(): Future[Unit] =
    for
      listenerRef <- server(port)
      _ = connectionsListener = Some(listenerRef)
      _ <- client(Map.empty)
    yield ()

  override def send(message: Export[DeviceId]): Unit =
    try outChannel.push((resolve() - deviceId).map(id => id -> message(id))) // TODO do not send to itself, right?
    catch case e: Channel.ChannelClosedException => scribe.error("The network manager was closed, cannot send message.")

  override def receive: Import[DeviceId] = Import(inValues.asScala.toMap)

  private def client(connections: Map[Endpoint, Connection]): Future[Unit] =
    for
      msgs <- outChannel.take
      nvalues <- Future(msgs.map((nid, valueTree) => nid.reachableAt.get -> valueTree)) // TODO: handle gracefully get
      newConnections <- Future.traverse(nvalues): (endpoint, valueTree) =>
        connections
          .get(endpoint)
          .filter(_.isOpen)
          .fold(establishConnection(endpoint))(Future.successful)
          .flatMap(conn => conn.sendOrClose((deviceId, valueTree)).map(_ => Right(endpoint -> conn)))
          .recover { case e => Left(e) }
      _ <- client(newConnections.collect { case Right(nc) => nc }.toMap)
    yield ()

  private def establishConnection(endpoint: Endpoint): Future[Connection] = out(endpoint)

  private def server(port: Port): Future[ListenerRef] = in(port)(inValues.put(_, _): Unit)

  override def close(): Unit = try
    outChannel.close()
    connectionsListener.foreach(_.listener.close())
  catch case e: (Channel.ChannelClosedException | Exception) => scribe.warn(e)

end SocketNetworkManager

object SocketNetworkManager:
  import it.unibo.scafi.runtime.network.CodableInstances.given

  def withFixedNeighbors[ID: BinaryCodable](
      deviceId: ID,
      port: Port,
      neighbors: Map[ID, Endpoint],
  )(using ExecutionContext, SocketConfiguration) = new SocketNetworkManager(deviceId, port)
    with SocketNetworking
    with InetAwareNeighborhoodResolver:

    override def resolve(): Neighborhood[DeviceId] = neighbors.keySet
    extension (id: ID) override def reachableAt: Option[Endpoint] = neighbors.get(id)

    val _ = start()
