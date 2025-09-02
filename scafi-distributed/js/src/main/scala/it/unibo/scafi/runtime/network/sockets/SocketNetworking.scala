package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.runtime.network.sockets.EventEmitter.*
import it.unibo.scafi.utils.Task

trait SocketNetworking(using ec: ExecutionContext, conf: SocketConfiguration) extends NetworkingTemplate:

  override def out(endpoint: Endpoint) = Task[Connection]:
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
      .onError(err => p.tryFailure(Exception(err.message)).pipe(_ => socket.destroy())): Unit

  override def in(port: Port)(onReceive: MessageIn => Unit) = Task[ListenerRef]:
    val listener = ServerSocketListener(onReceive)
    fromPromise[Listener]: p =>
      listener.serverSocket
        .onError(err => p.tryFailure(Exception(err.message)).pipe(_ => listener.serverSocket.close()))
        .listen(port)(() => p.trySuccess(listener): Unit)
    .map(ListenerRef(_, listener.accept))

  private class ServerSocketListener(onReceive: MessageIn => Unit) extends ListenerTemplate[Socket](onReceive):
    private var open = true
    private val acceptPromise: Promise[Unit] = Promise[Unit]()
    private val sendChannels = js.Map[Socket, ArrayBuffer[Byte]]()

    val serverSocket: Server = Net.createServer: socket =>
      socket
        .setTimeout(conf.inactivityTimeout.toIntMillis)(() => socket.destroy())
        .onData: chunk =>
          val buffer = sendChannels.getOrElseUpdate(socket, ArrayBuffer[Byte]())
          for i <- 0 until chunk.length do buffer += chunk(i).toByte
          serve(using socket)
        .onceClose(_ => sendChannels.remove(socket): Unit): Unit

    override def readMessageLength(using client: Socket): Try[Int] =
      val channel = sendChannels(client)
      Try(ByteBuffer.wrap(channel.slice(0, Integer.BYTES).toArray).getInt)
        .filter(channel.length >= Integer.BYTES + _)

    override def readMessage(length: Int)(using client: Socket): Array[Byte] =
      val buffer = sendChannels(client)
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
