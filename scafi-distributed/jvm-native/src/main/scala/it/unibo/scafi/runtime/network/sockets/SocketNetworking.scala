package it.unibo.scafi.runtime.network.sockets

import it.unibo.scafi.runtime.network.Serializable
import scala.concurrent.Future

trait SocketNetworking[T: Serializable] extends Networking[T, T] with InetTypes:

  override def in(port: Int)(onReceive: T => Unit): Future[ConnectionListener] = ???

  override def out(endpoint: (String, Int)): Future[Connection] = ???
