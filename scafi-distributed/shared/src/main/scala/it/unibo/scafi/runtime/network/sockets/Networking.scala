package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.Future

import it.unibo.scafi.utils.Task

/**
 * Networking platform-independent abstraction for connection-oriented communication.
 * @tparam MessageIn
 *   the incoming message type from remote processes.
 * @tparam MessageOut
 *   the outgoing message type to remote processes.
 */
trait Networking[+MessageIn, -MessageOut]:
  export InetTypes.*

  /**
   * A deferred [[Connection]] factory to a remote endpoint.
   * @param endpoint
   *   the remote [[Endpoint]] to connect to.
   * @return
   *   a function that, when invoked, attempt to establish a connection, returning a `Future` that successfully
   *   completes with the established connection, or fails with the reason of the failure in case of errors.
   */
  def out(endpoint: Endpoint): Task[Connection]

  /**
   * A deferred connection [[Listener]] factory that listens for incoming connections on a specific port.
   * @param port
   *   the port to listen on.
   * @param onReceive
   *   the callback to invoke when a message is received.
   * @return
   *   a function that, when invoked, attempts to create a connection listener, returning a `Future` that successfully
   *   completes with the [[ListenerRef]], or fails with the failure reason in case of errors.
   * @see
   *   ListenerRef
   */
  def in(port: Port)(onReceive: MessageIn => Unit): Task[ListenerRef]

  /**
   * Represents the state of a connection oriented resource.
   */
  trait ConnectionState:

    /** @return whether the connection is open or not. */
    def isOpen: Boolean

  /**
   * A remote closable connection.
   */
  trait Connection extends AutoCloseable with ConnectionState:

    /**
     * Sends the given message.
     * @param msg
     *   the message to send.
     * @return
     *   a `Future` that completes when the message has been sent successfully, or fails with the reason of the failure.
     */
    def send(msg: MessageOut): Future[Unit]

  /**
   * A reference to an active [[Listener]] and the asynchronous task responsible for managing incoming connections.
   * @param listener
   *   the listener managing the incoming connections.
   * @param accept
   *   the `Future` representing the asynchronous task accepting and handling incoming connections for this listener.
   */
  case class ListenerRef(listener: Listener, accept: Future[Unit])

  /**
   * A connection listener that binds to a specific port and listens for incoming connections.
   */
  trait Listener extends AutoCloseable with ConnectionState:

    /** @return the port this listener is bound to. */
    def boundPort: Port
end Networking
