package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.Future

trait Networking[+MessageIn, -MessageOut] extends InetTypes:

  /**
   * A deferred [[Connection]] factory to a remote endpoint.
   * @param endpoint
   *   the remote [[Endpoint]] to connect to.
   * @return
   *   a function that, when invoked, attempt to establish a connection, returning a `Future` that successfully
   *   completes with the established connection, or fails with the reason of the failure in case of errors.
   */
  def out(endpoint: Endpoint): () => Future[Connection]

  /**
   * A deferred connections [[Listener]] factory that listens for incoming connections on a specific port.
   * @param port
   *   the port to listen on.
   * @param onReceive
   *   the callback to invoke when a message is received.
   * @return
   *   a function that, when invoked, attempt to create a connection listener, returning a `Future` that successfully
   *   completes with the [[ListenerRef]], or fails with the reason of the failure in case of errors.
   * @see
   *   ListenerRef
   */
  def in(port: Port)(onReceive: MessageIn => Unit): () => Future[ListenerRef]

  trait ConnectionState:
    def isOpen: Boolean

  trait Connection extends AutoCloseable with ConnectionState:
    def send(msg: MessageOut): Future[Unit]

  case class ListenerRef(listener: Listener, accept: Future[Unit])

  trait Listener extends AutoCloseable with ConnectionState:
    def boundPort: Port
end Networking
