package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.chaining.scalaUtilChainingOps

trait SocketNetworking(using ec: ExecutionContext, conf: ConnectionConfiguration) extends ConnectionOrientedTemplate:

  override def out(endpoint: Endpoint): Future[Connection] =
    for
      socket <- createSocket(endpoint)
      conn = new ConnectionTemplate:
        override def write(buffer: Array[Byte]): Future[Unit] = fromPromise: p =>
          val data = new Uint8Array(buffer.length)
          for i <- buffer.indices do data(i) = buffer(i)
          socket.write(data):
            case e: js.Error => p.tryFailure(Exception(e.message)): Unit
            case _ => p.trySuccess(()): Unit
        override def isOpen: Boolean = !socket.destroyed
        override def close(): Unit = socket.destroy()
    yield conn

  private def createSocket(endpoint: Endpoint): Future[Socket] = fromPromise: p =>
    val socket = Net.connect(endpoint.port, endpoint.address)
    socket
      .onceConnect(() => p.trySuccess(socket): Unit)
      .onceClose(_ => p.tryFailure(Exception("Socket server closed connection")).pipe(_ => socket.destroy()))
      .onError(err => p.tryFailure(Exception(err.message)).pipe(_ => socket.destroy())): Unit

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
          for i <- 0 until chunk.length do buffer += chunk(i).toByte
          serve(using socket): Unit
        .onceClose(_ => clientChannels.remove(socket): Unit): Unit

    override def readMessageLength(using client: Socket): Int =
      val channel = clientChannels(client)
      ByteBuffer.wrap(channel.slice(0, Integer.BYTES).toArray).getInt ensuring (channel.length >= Integer.BYTES + _)

    override def readMessage(length: Int)(using client: Socket): Array[Byte] =
      val buffer = clientChannels(client)
      val msgBytes = buffer.slice(Integer.BYTES, Integer.BYTES + length).toArray
      buffer.remove(0, Integer.BYTES + length)
      msgBytes

    override def accept: Future[Unit] = acceptPromise.future

    override def boundPort: Port = serverSocket.address().port.assume

    override def close(): Unit =
      open = false
      serverSocket.close()
      acceptPromise.trySuccess(()): Unit

    override def isOpen: Boolean = open
  end ServerSocketListener

  private def fromPromise[T](logic: Promise[T] => Unit): Future[T] = Promise[T].tap(logic).future
end SocketNetworking
