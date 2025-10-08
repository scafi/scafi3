package it.unibo.scafi.runtime.network.sockets

import java.io.{ DataInputStream, DataOutputStream, InputStream }
import java.net.{ ServerSocket, Socket }
import java.util.concurrent.ConcurrentHashMap

import scala.LazyList.continually
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Success, Try }

trait SocketNetworking(using ec: ExecutionContext, conf: ConnectionConfiguration) extends ConnectionOrientedTemplate:

  override def out(endpoint: Endpoint): Future[Connection] =
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

  override def in(port: Port)(onReceive: MessageIn => Unit): Future[ListenerRef] =
    for
      server <- Future(ServerSocket(port))
      listener = new ListenerTemplate[Socket](onReceive):
        private val clientChannels = ConcurrentHashMap[Socket, InputStream]()
        override val accept = Future:
          continually(Try(server.accept))
            .takeWhile(_.isSuccess)
            .collect { case Success(s) => s }
            .foreach: s =>
              clientChannels.put(s, s.getInputStream)
              s.setSoTimeout(conf.inactivityTimeout.toIntMillis)
              Future.fromTry(serve(using s)).onComplete(_ => clientChannels.remove(s))
          clientChannels.keySet.forEach(_.close())
        override def readMessageLength(using client: Socket): Int =
          DataInputStream(clientChannels.get(client)).readInt ensuring (_ > -1)
        override def readMessage(length: Int)(using client: Socket): Array[Byte] =
          clientChannels.get(client).readNBytes(length)
        override def close(): Unit = server.close()
        override def isOpen: Boolean = !server.isClosed
        override def boundPort: Port = server.getLocalPort.assume
    yield ListenerRef(listener, listener.accept)
end SocketNetworking
