package it.unibo.scafi.runtime.network.sockets

import java.net.{ ServerSocket, Socket, SocketException }
import java.io.{ DataInputStream, DataOutputStream }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.*
import scala.util.boundary.break
import scala.util.chaining.scalaUtilChainingOps
import scala.annotation.tailrec

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*

trait SocketNetworking[T: Serializable](using ExecutionContext) extends Networking[T, T] with InetTypes:

  private val MAX_MESSAGE_SIZE = 1024 * 1024

  override def in(port: Port)(onReceive: T => Unit): () => Future[ListenerRef] = () =>
    for
      server <- Future.fromTry(Try(ServerSocket(port)))
      serve = (client: Socket) =>
        val in = DataInputStream(client.getInputStream())
        boundary:
          while true do
            val msgLen = in.readInt()
            if msgLen < 0 || msgLen > MAX_MESSAGE_SIZE then
              client.close()
              break()
            val buffer = new Array[Byte](msgLen)
            in.readFully(buffer, 0, msgLen)
            val msg = deserialize(buffer.take(msgLen))
            onReceive(msg)
        client.close()
      accept = Future:
        @tailrec
        def acceptLoop(): Unit =
          Try(server.accept()) match
            case Success(socket) =>
              val _ = Future(serve(socket))
              acceptLoop()
            case Failure(_: SocketException) => ()
            case _ => acceptLoop()
        acceptLoop()
      listener = new Listener:
        override def close(): Unit = server.close()
        override def isOpen: Boolean = !server.isClosed
        override def boundPort: Port = server.getLocalPort()
      listenerRef = ListenerRef(listener, accept)
    yield listenerRef

  override def out(endpoint: (Address, Port)): () => Future[Connection] = () =>
    for
      socket <- Future.fromTry(Try(Socket(endpoint._1, endpoint._2)))
      conn = new Connection:
        override def send(msg: T): Future[Unit] = Future.fromTry:
          serialize(msg).pipe(s => Try(DataOutputStream(socket.getOutputStream()).tap(_.writeInt(s.length)).write(s)))
        override def close(): Unit = socket.close()
        override def isOpen: Boolean = !socket.isClosed
    yield conn
end SocketNetworking
