package it.unibo.scafi.runtime.network.sockets

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.Using.Releasable

/**
 * Node.js emitter of events that can be listened to.
 * @see
 *   [[https://nodejs.org/api/events.html#class-eventemitter nodejs documentation]]
 */
@js.native
trait EventEmitter extends js.Object:

  /**
   * Adds a listener for the specified event.
   * @param eventName
   *   the name of the event to listen for.
   * @param listener
   *   the function to be called when the event is emitted. No checks are made to see if the listener is already
   *   registered.
   * @return
   *   the EventEmitter instance itself.
   */
  def on(eventName: String)(listener: js.Function): this.type = js.native

  /**
   * Adds a one-time listener for the specified event.
   * @param eventName
   *   the name of the event to listen for.
   * @param listener
   *   the function to be called when the event is emitted. The next time the event is triggered this listener is
   *   removed and then invoked.
   * @return
   *   the EventEmitter instance itself.
   */
  def once(eventName: String)(listener: js.Function): this.type = js.native
end EventEmitter

/**
 * A facade for the Node.js `net` module, which provides an asynchronous network API for creating stream-based TCP or
 * IPC servers and clients.
 * @see
 *   [[https://nodejs.org/api/net.html#net nodejs documentation]]
 */
@js.native
@JSImport("net", JSImport.Namespace)
object Net extends js.Object:

  /**
   * A factory function which creates a new Socket connection.
   * @param port
   *   the port to which the socket should connect.
   * @param host
   *   the host to which the socket should connect.
   * @return
   *   the newly created [[Socket]] instance.
   */
  def connect(port: Int, host: String): Socket = js.native

  /**
   * A factory function which creates a new TPC or IPC server.
   * @param connectionListener
   *   the function to be called when a new connection is established.
   * @return
   *   the newly created [[Server]] instance.
   */
  def createServer(connectionListener: js.Function1[Socket, Unit]): Server = js.native
end Net

/**
 * An [[EventEmitter]] abstraction used to create a TCP or IPC server.
 * @see
 *   [[https://nodejs.org/api/net.html#class-netserver nodejs documentation]]
 */
@js.native
trait Server extends EventEmitter:

  @js.native
  trait Address extends js.Object:
    /** The port on which the server is listening to. */
    val port: Int

    /** The family of the address, e.g., `IPv4` or `IPv6`. */
    val family: String

    /** The address of the server, e.g., `127.0.0.1`. */
    val address: String

  /**
   * Start a server listening for connections.
   * @param port
   *   the port on which the server should listen for incoming connections. If set to 0 the operating system will assign
   *   an arbitrary unused port.
   * @param callback
   *   the one-time listener called when the server starts listening.
   */
  def listen(port: Int)(callback: js.Function0[Unit]): Unit = js.native

  /** @return the bound [[Address]] of the server. */
  def address(): Address = js.native

  /** Stops the server from accepting new connections and keeps existing connections. */
  def close(): Unit = js.native
end Server

/**
 * An [[EventEmitter]] abstraction of a TCP socket or a streaming IPC endpoint.
 * @see
 *   [[https://nodejs.org/api/net.html#class-netsocket nodejs documentation]]
 */
@js.native
trait Socket extends EventEmitter:

  /** @return the string representation of the remote IP address. */
  def remoteAddress: String = js.native

  /** @return the remote port. */
  def remotePort: Int = js.native

  /**
   * Sends data on the socket.
   * @param buffer
   *   buffer containing the data to be sent.
   * @param callback
   *   callback executed when the data is finally written out, which may not be immediately.
   */
  def write(buffer: Uint8Array)(callback: js.Function1[js.Error | Unit, Unit]): Unit = js.native

  /**
   * Sets the socket to timeout after a specified number of milliseconds.
   * @param timeout
   *   the number of milliseconds after which the socket will timeout. If set to 0, the timeout is disabled.
   * @param callback
   *   callback to be executed when the timeout occurs: users need to manually destroy to end the connection.
   * @return
   *   the socket instance itself.
   */
  def setTimeout(timeout: Int)(callback: js.Function0[Unit]): Socket = js.native

  /** Destroys the stream and closes the connection ensuring no more I/O activity happens on this socket. */
  def destroy(): Unit = js.native

  /**
   * @return
   *   a boolean indicating if the connection is destroyed or not. Once a connection is destroyed no further data can be
   *   transferred using it.
   */
  def destroyed: Boolean = js.native
end Socket

object Socket:

  given Releasable[Socket] with
    override def release(resource: Socket): Unit = resource.destroy()

object EventEmitter:

  extension (socket: Socket)

    /**
     * Registers a listener for the `data` event.
     * @param listener
     *   the function to be called when data is received on the socket.
     * @return
     *   the socket instance itself.
     */
    infix def onData(listener: js.Function1[Uint8Array, Unit]): Socket = socket.on("data")(listener)

    /**
     * Registers a one-time listener for the `close` event.
     * @param listener
     *   the function to be called when the socket is closed. It receives a boolean indicating if the socket was closed
     *   gracefully or had an error.
     * @return
     *   the socket instance itself.
     */
    infix def onceClose(listener: Boolean => Unit): Socket = socket.once("close")(listener)

    /**
     * Registers a one-time listener for the `connect` event.
     * @param listener
     *   the function to be called when the socket connects.
     * @return
     *   the socket instance itself.
     */
    infix def onceConnect(listener: () => Unit): Socket = socket.on("connect")(listener)
  end extension

  extension (emitter: Socket | Server)

    /**
     * Registers a listener for the `error` event.
     * @param listener
     *   the function to be called when an error occurs on the socket.
     * @return
     *   the emitter instance itself.
     */
    infix def onError(listener: js.Function1[js.Error, Unit]): emitter.type = emitter.on("error")(listener)
end EventEmitter
