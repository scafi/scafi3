package it.unibo.scafi.runtime.network.sockets

import java.net.{ ServerSocket, Socket, SocketException }
import java.io.{ DataInputStream, DataOutputStream }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.*
import scala.LazyList.continually

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*

trait SocketNetworking[Message: Serializable](using ec: ExecutionContext, conf: SocketConfiguration)
    extends NetworkingTemplate[Message]:

  override def out(endpoint: Endpoint): () => Future[Connection] = () =>
    for
      socket <- Future(Socket(endpoint._1, endpoint._2))
      conn = new ConnectionTemplate:
        private val sendChannel = DataOutputStream(socket.getOutputStream)
        override def write(buffer: Array[Byte]): Future[Unit] = Future:
          synchronized:
            sendChannel.write(buffer)
            sendChannel.flush()
        override def close(): Unit = (sendChannel :: socket :: Nil).foreach(_.close)
        override def isOpen: Boolean = !socket.isClosed && Try(synchronized(sendChannel.write(0))).isSuccess
    yield conn

  override def in(port: Port)(onReceive: Message => Unit): () => Future[ListenerRef] = () =>
    for
      server <- Future(ServerSocket(port))
      serve = (client: Socket) =>
        Using.resource(client): c =>
          c.setSoTimeout(conf.inactivityTimeout.toMillis.toInt)
          val recvChannel = DataInputStream(c.getInputStream)
          continually(recvChannel.readInt)
            .takeWhile(msgLen => msgLen > -1 && msgLen < conf.maxMessageSize)
            .filter(_ > 0)
            .map(msgLen => deserialize(recvChannel.readNBytes(msgLen)))
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
