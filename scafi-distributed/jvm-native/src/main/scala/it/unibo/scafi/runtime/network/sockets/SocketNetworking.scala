package it.unibo.scafi.runtime.network.sockets

import java.io.{ DataInputStream, DataOutputStream }
import java.net.{ ServerSocket, Socket, SocketException }

import scala.LazyList.continually
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

import it.unibo.scafi.utils.Task

trait SocketNetworking(using ec: ExecutionContext, conf: SocketConfiguration) extends ConnectionOrientedTemplate:

  override def out(endpoint: Endpoint) = Task:
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

  override def in(port: Port)(onReceive: MessageIn => Unit) = Task[ListenerRef]:
    for
      server <- Future(ServerSocket(port))
      listener = new ListenerTemplate[Socket](onReceive):
        override val accept = Future:
          continually(Try(server.accept))
            .takeWhile:
              case Failure(_: SocketException) => false
              case _ => true
            .collect { case Success(c) => c }
            .foreach: s =>
              s.setSoTimeout(conf.inactivityTimeout.toIntMillis)
              Future(serve(using s))

        override def readMessageLength(using client: Socket): Try[Int] =
          Try(DataInputStream(client.getInputStream).readInt).filter(_ > -1)
        override def readMessage(length: Int)(using client: Socket): Array[Byte] =
          client.getInputStream.readNBytes(length)
        override def close(): Unit = server.close()
        override def isOpen: Boolean = !server.isClosed
        override def boundPort: Port = server.getLocalPort.assume
    yield ListenerRef(listener, listener.accept)
end SocketNetworking
