package it.unibo.scafi.runtime.network.sockets

import java.net.{ ServerSocket, Socket, SocketException }
import java.io.{ DataInputStream, DataOutputStream }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.*
import scala.LazyList.continually

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*

trait SocketNetworking[T: Serializable](using ec: ExecutionContext, conf: SocketConfiguration)
    extends Networking[T, T]
    with InetTypes:

  override def out(endpoint: (Address, Port)): () => Future[Connection] = () =>
    for
      socket <- Future.fromTry(Try(Socket(endpoint._1, endpoint._2)))
      conn = new Connection:
        override def send(msg: T): Future[Unit] = Future:
          val serializedMsg = serialize(msg)
          val sendChannel = DataOutputStream(socket.getOutputStream)
          sendChannel.writeInt(serializedMsg.length)
          sendChannel.write(serializedMsg)
        override def close(): Unit = socket.close()
        override def isOpen: Boolean = !socket.isClosed
    yield conn

  override def in(port: Port)(onReceive: T => Unit): () => Future[ListenerRef] = () =>
    for
      server <- Future.fromTry(Try(ServerSocket(port)))
      serve = (client: Socket) =>
        Using.resource(client): c =>
          c.setSoTimeout(conf.connectionTimeout.toMillis.toInt)
          val in = DataInputStream(c.getInputStream)
          continually(in.readInt)
            .takeWhile(msgLen => msgLen > 0 && msgLen < conf.maxMessageSize)
            .map(msgLen => deserialize(in.readNBytes(msgLen)))
            .foreach(onReceive)
      accept = Future:
        continually(Try(server.accept))
          .takeWhile:
            case Failure(_: SocketException) => false
            case _ => true
          .collect { case Success(c) => c }
          .foreach(s => Future(serve(s)))
      listener = new Listener:
        override def close(): Unit = server.close()
        override def isOpen: Boolean = !server.isClosed
        override def boundPort: Port = server.getLocalPort
    yield ListenerRef(listener, accept)
end SocketNetworking
