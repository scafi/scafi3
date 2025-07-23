package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.{ ExecutionContext, Future }
import scala.jdk.CollectionConverters.*

import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.utils.Channel
import it.unibo.scafi.utils.Channel.ChannelClosedException

trait SocketBasedNetworkManager(using ExecutionContext) extends NetworkManager:
  self: Networking & InetAwareNeighborhoodResolver =>

  private type Envelope = (DeviceId, ValueTree)

  override type MessageIn = Envelope

  override type MessageOut = Envelope

  private val outChannel = Channel[MessageOut]
  private val inValues = ConcurrentHashMap[DeviceId, ValueTree]()

  val tasks = client(Map.empty) :: server(8080) :: Nil

  override def send(message: Export[DeviceId]): Unit =
    try message.devices.map(id => id -> message(id)).foreach(outChannel.push)
    catch case e: ChannelClosedException => () // TODO what to do here?

  override def receive: Import[DeviceId] = Import(inValues.asScala.toMap)

  private def client(connections: Map[Endpoint, Connection])(using ExecutionContext): Future[Unit] =
    for
      msg <- outChannel.take
      neighbors <- Future.successful(reachableNeighbors)
      newConnections <- Future.traverse(neighbors): n =>
        connections
          .get(n)
          .filter(_.isOpen)
          .fold(establishConnection(n))(Future.successful)
          .flatMap(conn => conn.send(msg).map(_ => Right(n -> conn)))
          .recover { case e => Left(e) }
      _ <- client(newConnections.collect { case Right(nc) => nc }.toMap)
    yield ()

  private def establishConnection(endpoint: Endpoint): Future[Connection] = out(endpoint).run()

  private def server(port: Port): Future[ListenerRef] = in(port):
    case (id, valueTree) => inValues.put(id, valueTree): Unit
  .run()

end SocketBasedNetworkManager
