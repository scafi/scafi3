package it.unibo.scafi.runtime.network.sockets

import java.net.{ ServerSocket, Socket }

import scala.collection.Iterator.continually
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext

import it.unibo.scafi.runtime.network.Serializable

trait SocketNetworking[T: Serializable](using ExecutionContext) extends Networking[T, T] with InetTypes:

  override def in(port: Int)(onReceive: T => Unit): Future[ConnectionListener] = Future.fromTry:
    for
      socketServer <- Try(ServerSocket(port))
      connListener = new ConnectionListener:
        val acceptLoop = Future:
          continually(Try(socketServer.accept)).filter(_.isSuccess).foreach(s => serve(s.get))

        private def serve(client: Socket) = ???

        override def close(): Unit = socketServer.close()
        override def isOpen: Boolean = !socketServer.isClosed
    yield connListener

  override def out(endpoint: (String, Int)): Future[Connection] = ???
