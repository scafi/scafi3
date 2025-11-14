package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.scalajs.js
import scala.util.{ Success, Try }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.runtime.network.sockets.InetTypes.{ assume, Endpoint, Port }
import it.unibo.scafi.utils.Uint8ArrayOps.{ toByteArray, toUint8Array }

trait SocketNetworking(using ec: ExecutionContext, conf: ConnectionConfiguration) extends ConnectionOrientedTemplate:

  override def out(endpoint: Endpoint): Future[Connection] = createSocket(endpoint).map(ConnectionImpl.apply)

  private def createSocket(endpoint: Endpoint): Future[Socket] = fromPromise: p =>
    val socket = Net.connect(endpoint.port, endpoint.address)
    socket
      .onceConnect(() => p.trySuccess(socket): Unit)
      .onceClose(_ => p.tryFailure(Exception("Socket server closed connection")).pipe(_ => socket.destroy()))
      .onError(err => p.tryFailure(Exception(err.message)).pipe(_ => socket.destroy())): Unit

  private class ConnectionImpl(socket: Socket) extends ConnectionTemplate:
    override def write(buffer: Array[Byte]): Future[Unit] = fromPromise: p =>
      socket.write(buffer.toUint8Array):
        case e: js.Error => p.tryFailure(Exception(e.message)): Unit
        case _ => p.trySuccess(()): Unit

    override def isOpen: Boolean = !socket.destroyed

    override def close(): Unit = socket.destroy()

  override def in(port: Port)(onReceive: MessageIn => Unit): Future[ListenerRef] =
    val listener = ServerSocketListener(onReceive)
    fromPromise[Listener]: p =>
      listener.serverSocket
        .onError(err => p.tryFailure(Exception(err.message)).pipe(_ => listener.serverSocket.close()))
        .listen(port)(() => p.trySuccess(listener): Unit)
    .map(ListenerRef(_, listener.accept))

  private class ServerSocketListener(onReceive: MessageIn => Unit) extends ListenerTemplate[Socket](onReceive):
    private var open = true
    private val acceptPromise: Promise[Unit] = Promise[Unit]()
    private val clientChannels = js.Map[Socket, ArrayBuffer[Byte]]()

    val serverSocket: Server = Net.createServer: socket =>
      socket
        .setTimeout(conf.inactivityTimeout.toIntMillis)(() => socket.destroy())
        .onData: chunk =>
          val buffer = clientChannels.getOrElseUpdate(socket, ArrayBuffer[Byte]())
          buffer.addAll(chunk.toByteArray)
          serve(using socket).fold(_ => socket.destroy(), _ => ())
        .onceClose(_ => clientChannels.remove(socket): Unit): Unit

    override def readMessageLength(using client: Socket): Try[Option[Int]] =
      val channel = clientChannels(client)
      if channel.length >= Integer.BYTES then
        channel.read(Integer.BYTES).map(bytes => Some(ByteBuffer.wrap(bytes).getInt))
      else Success(None)

    override def readMessage(length: Int)(using client: Socket): Try[Array[Byte]] = clientChannels(client).read(length)

    extension (buffer: ArrayBuffer[Byte])
      private def read(length: Int): Try[Array[Byte]] = Try:
        val bytes = buffer.slice(from = 0, until = length).toArray
        buffer.remove(index = 0, count = length)
        bytes

    override def accept: Future[Unit] = acceptPromise.future

    override def boundPort: Port = serverSocket.address().port.assume

    override def close(): Unit =
      open = false
      clientChannels.keys.foreach(s => if !s.destroyed then s.destroy())
      serverSocket.close(() => acceptPromise.trySuccess(()): Unit)

    override def isOpen: Boolean = open
  end ServerSocketListener

  private def fromPromise[T](logic: Promise[T] => Unit): Future[T] = Promise[T].tap(logic).future
end SocketNetworking
