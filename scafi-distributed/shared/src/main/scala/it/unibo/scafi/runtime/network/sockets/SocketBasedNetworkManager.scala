package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.{ ExecutionContext, Future }
import scala.jdk.CollectionConverters.*

import it.unibo.scafi.message.*
import it.unibo.scafi.message.Codable.*
import it.unibo.scafi.runtime.network.{ Neighborhood, NetworkManager }
import it.unibo.scafi.runtime.network.sockets.InetTypes.Endpoint
import it.unibo.scafi.utils.Channel

import io.bullet.borer.Cbor
import io.github.iltotore.iron.*

// TODO: better port management
trait SocketBasedNetworkManager[ID](deviceId: ID, port: Int)(using ExecutionContext)
    extends NetworkManager
    with Networking
    with AutoCloseable:
  self: InetAwareNeighborhoodResolver =>

  override type DeviceId = ID

  private type Envelope = (DeviceId, ValueTree)

  override type MessageIn = Envelope

  override type MessageOut = Envelope

  private val outChannel = Channel[Set[MessageOut]]
  private val inValues = ConcurrentHashMap[DeviceId, ValueTree]()
  private var connectionsListener: Option[ListenerRef] = None

  // TODO: do not care result?
  def start(): Future[Unit] = for
    listenerRef <- server(port.refineUnsafe)
    _ = connectionsListener = Some(listenerRef)
    _ <- client(Map.empty)
  yield ()

  override def send(message: Export[DeviceId]): Unit =
    try outChannel.push((resolve() - deviceId).map(id => id -> message(id))) // TODO do not send to itself, right?
    catch case e: Channel.ChannelClosedException => scribe.warn(e) // TODO what to do here?

  override def receive: Import[DeviceId] = Import(inValues.asScala.toMap)

  private def client(connections: Map[Endpoint, Connection]): Future[Unit] =
    for
      msgs <- outChannel.take
      nvalues <- Future(msgs.map((id, valueTree) => (id.reachableAt.get, valueTree))) // TODO: handle gracefully get
      newConnections <- Future.traverse(nvalues): (endpoint, value) =>
        connections
          .get(endpoint)
          .filter(_.isOpen)
          .fold(establishConnection(endpoint))(Future.successful)
          .flatMap(conn => conn.send((deviceId, value)).map(_ => Right(endpoint -> conn))) // TODO: close on error?
          .recover { case e => Left(e) }
      _ <- client(newConnections.collect { case Right(nc) => nc }.toMap)
    yield ()

  private def establishConnection(endpoint: Endpoint): Future[Connection] = out(endpoint).run()

  private def server(port: Port): Future[ListenerRef] = in(port): messageIn =>
    val (id, valueTree) = messageIn
    inValues.put(id, valueTree): Unit
  .run()

  override def close(): Unit =
    try
      outChannel.close()
      connectionsListener.foreach(_.listener.close())
    catch case e: (Channel.ChannelClosedException | Exception) => scribe.warn(e)

end SocketBasedNetworkManager

object SocketBasedNetworkManager:
  import it.unibo.scafi.runtime.network.CodableInstances.given

  def withStaticallyAssignedNeighbors[ID: BinaryCodable](deviceId: ID, port: Int, neighbors: Map[ID, Endpoint])(using
      executionContext: ExecutionContext,
      socketConfiguration: SocketConfiguration,
  ) = new SocketBasedNetworkManager(deviceId, port) with SocketNetworking with InetAwareNeighborhoodResolver:

    val _ = start()

    override def resolve(): Neighborhood[DeviceId] = neighbors.keySet
    extension (id: ID) override def reachableAt: Option[Endpoint] = neighbors.get(id)

    override given BinaryEncodable[MessageOut] = msg =>
      val (id, valueTree) = msg
      val encodedId = encode(id)
      val encodedPathsWithValues = valueTree.paths
        .map(path => encode(path) -> valueTree.get[Array[Byte]](path)) // TODO apply?
        .filter((_, optionalValue) => optionalValue.isDefined)
        .map { case (pathBytes, value) => pathBytes -> value.get }
        .toMap
      Cbor.encode((encodedId, encodedPathsWithValues)).toByteArray

    override given BinaryDecodable[MessageIn] = bytes =>
      val (rawId, rawValueTree) = Cbor.decode(bytes).to[(Array[Byte], Map[Array[Byte], Array[Byte]])].value
      val valueTree = ValueTree(rawValueTree.map(decode[Array[Byte], Path](_) -> _))
      decode(rawId) -> valueTree
end SocketBasedNetworkManager
