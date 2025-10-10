package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{ ExecutionContext, Future }
import scala.jdk.CollectionConverters.MapHasAsScala

import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.runtime.network.sockets.InetTypes.Port
import it.unibo.scafi.utils.Channel

/**
 * A [[NetworkManager]] using a connection-oriented networking stack for communicating with neighbors.
 * @tparam ID
 *   the type of the self's device identifier.
 */
trait ConnectionOrientedNetworkManager[ID](deviceId: ID, port: Port)(using ExecutionContext)
    extends NetworkManager
    with ConnectionOrientedNetworking
    with AutoCloseable:
  self: InetAwareNeighborhoodResolver =>

  override type DeviceId = ID

  type Envelope = (DeviceId, ValueTree)

  override type MessageIn = Envelope

  override type MessageOut = Envelope

  private val outChannel = Channel[Set[MessageOut]]
  private val inValues = ConcurrentHashMap[DeviceId, ValueTree]()
  private val connectionsListener = AtomicReference[ListenerRef]()

  /**
   * Starts the network manager, establishing a server socket to listen for incoming connections on the specified port.
   * @return
   *   a `Future` that completes when the network manager is successfully started, or fails if any error occurs.
   */
  def start(): Future[Unit] =
    for
      _ <- if outChannel.isClosed then Future.failed(IllegalStateException("Network manager's closed")) else Future.unit
      listenerRef <- server(port)
      _ = connectionsListener.set(listenerRef)
      _ = client(Map.empty)
    yield ()

  override def send(message: Export[DeviceId]): Unit =
    try outChannel.push(neighborhood.map(id => id -> message(id)))
    catch case _: Channel.ChannelClosedException => scribe.error("The network manager is closed, cannot send message.")

  override def receive: Import[DeviceId] = Import(inValues.asScala.toMap)

  private def client(connections: Map[Endpoint, Connection]): Future[Unit] =
    (for
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
    yield ()).andThen(_ => connections.values.foreach(_.close()))

  private def establishConnection(endpoint: Endpoint): Future[Connection] = out(endpoint)

  private def server(port: Port): Future[ListenerRef] = in(port)(inValues.put(_, _): Unit)

  override def close(): Unit =
    try
      outChannel.close()
      Option(connectionsListener.get()).foreach(_.listener.close())
    catch
      case _: Channel.ChannelClosedException => scribe.error("The network manager is already closed ")
      case e: Exception => scribe.error(e)

  /** @return the port this network manager is bound to, if any. */
  def boundPort: Option[Port] = Option(connectionsListener.get()).map(_.listener.boundPort)

end ConnectionOrientedNetworkManager
