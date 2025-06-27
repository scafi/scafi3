package it.unibo.scafi.runtime.network.sockets

import java.io.DataInputStream
import java.net.{ ServerSocket, Socket }
import java.io.DataOutputStream

import scala.collection.Iterator.continually
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext
import scala.util.boundary
import scala.util.boundary.break
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.{ deserialize, serialize }

trait SocketNetworking[T: Serializable](using ExecutionContext) extends Networking[T, T] with InetTypes:

  private val MAX_MESSAGE_SIZE = 1024 * 1024

  override def in(port: Port)(onReceive: T => Unit): Future[ConnectionListener] = Future.fromTry:
    for
      socketServer <- Try(ServerSocket(port))
      connListener = new ConnectionListener:
        val acceptLoop = Future:
          continually(Try(socketServer.accept)).filter(_.isSuccess).foreach(s => serve(s.get))
        private def serve(client: Socket) =
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
        override def close(): Unit = socketServer.close()
        override def isOpen: Boolean = !socketServer.isClosed
        override def boundPort: Port = socketServer.getLocalPort()
    yield connListener

  override def out(endpoint: (Address, Port)): Future[Connection] = Future.fromTry:
    for
      socket <- Try(Socket(endpoint._1, endpoint._2))
      conn = new Connection:
        override def send(msg: T): Future[Unit] = Future.fromTry:
          serialize(msg).pipe(s => Try(DataOutputStream(socket.getOutputStream()).tap(_.writeInt(s.length)).write(s)))
        override def close(): Unit = socket.close()
        override def isOpen: Boolean = !socket.isClosed
    yield conn
end SocketNetworking
