package it.unibo.scafi.runtime.network.sockets

import java.io.{ DataInputStream, DataOutputStream }
import java.net.{ ServerSocket, Socket }
import java.util.concurrent.ConcurrentHashMap

import scala.LazyList.continually
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Success, Try }
import scala.util.chaining.scalaUtilChainingOps

trait SocketNetworking(using ec: ExecutionContext, conf: ConnectionConfiguration) extends ConnectionOrientedTemplate:

  override def out(endpoint: Endpoint): Future[Connection] =
    Future(Socket(endpoint._1, endpoint._2)).map(ConnectionImpl.apply)

  private class ConnectionImpl(socket: Socket) extends ConnectionTemplate:
    private val sendChannel = DataOutputStream(socket.getOutputStream)

    override def write(data: Array[Byte]): Future[Unit] = Future(syncWrite(data))

    override def close(): Unit = (sendChannel :: socket :: Nil).foreach(_.close)

    override def isOpen: Boolean = !socket.isClosed && Try(syncWrite(0.toBytes)).isSuccess

    private def syncWrite(data: Array[Byte]): Unit = synchronized:
      sendChannel.write(data)
      sendChannel.flush()

  override def in(port: Port)(onReceive: MessageIn => Unit): Future[ListenerRef] =
    Future(ServerSocket(port)).map(server => ListenerImpl(server, onReceive)).map(l => ListenerRef(l, l.accept))

  private class ListenerImpl(server: ServerSocket, onReceive: MessageIn => Unit)
      extends ListenerTemplate[Socket](onReceive):
    private val clientChannels = ConcurrentHashMap[Socket, DataInputStream]()

    override val accept = Future:
      continually(Try(server.accept))
        .takeWhile(_.isSuccess)
        .collect { case Success(s) => s }
        .foreach(handle)
      clientChannels.keySet.forEach(_.close())

    private def handle(client: Socket): Unit =
      clientChannels.put(client, DataInputStream(client.getInputStream))
      client.setSoTimeout(conf.inactivityTimeout.toIntMillis)
      Future(serve(using client)).onComplete(_ => client.tap(clientChannels.remove).close())

    override def readMessageLength(using client: Socket): Try[Option[Int]] = Try:
      Option(clientChannels.get(client).readInt).filter(_ > -1)

    override def readMessage(length: Int)(using client: Socket): Try[Array[Byte]] = Try:
      clientChannels.get(client).readNBytes(length)

    override def close(): Unit = server.close()

    override def isOpen: Boolean = !server.isClosed

    override def boundPort: Port = server.getLocalPort.assume
  end ListenerImpl
end SocketNetworking
