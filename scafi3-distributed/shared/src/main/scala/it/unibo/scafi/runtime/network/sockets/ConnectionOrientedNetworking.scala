package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.Future

import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, Port }

/**
 * Networking platform-independent abstraction for connection-oriented networking operations.
 */
trait ConnectionOrientedNetworking:

  /** The incoming message type from remote processes. */
  type MessageIn

  /** The outgoing message type to remote processes. */
  type MessageOut

  /**
   * A [[Connection]] factory to a remote endpoint.
   * @param endpoint
   *   the remote [[Endpoint]] to connect to.
   * @return
   *   a `Future` that attempts to establish a connection to the provided remote endpoint, completing with the
   *   established connection on success, or fails in case of errors.
   */
  def out(endpoint: Endpoint): Future[Connection]

  /**
   * A connection [[Listener]] factory that listens for incoming connections on a specific port.
   * @param port
   *   the port to listen on.
   * @param onReceive
   *   the callback to invoke when a message is received.
   * @return
   *   a `Future` that attempts to create a connection listener, completing with the [[ListenerRef]] on success, or
   *   fails in case of errors.
   * @see
   *   [[ListenerRef]]
   */
  def in(port: Port)(onReceive: MessageIn => Unit): Future[ListenerRef]

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
     *   a `Future` that completes when the message has been sent successfully, or fails in case of errors.
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

end ConnectionOrientedNetworking
