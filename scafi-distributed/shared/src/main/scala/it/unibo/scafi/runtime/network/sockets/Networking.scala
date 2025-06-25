package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.Future

trait Networking[+MessageIn, -MessageOut]:
  ctx: InetTypes =>

  def out(endpoint: Endpoint): Future[Connection]

  def in(port: Port)(onReceive: MessageIn => Unit): Future[ConnectionListener]

  trait ConnectionState:
    def isOpen: Boolean

  trait Connection extends AutoCloseable with ConnectionState:
    def send(msg: MessageOut): Future[Unit]

  trait ConnectionListener extends AutoCloseable with ConnectionState
