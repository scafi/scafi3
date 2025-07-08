package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.LazyList.continually
import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.collection.mutable.ArrayBuffer
import scala.util.{ Failure, Success, Try }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*
import it.unibo.scafi.runtime.network.sockets.EventEmitter.*

trait SocketNetworking[Message: Serializable](using ec: ExecutionContext, conf: SocketConfiguration)
    extends NetworkingTemplate[Message]:

  override def out(endpoint: Endpoint): () => Future[Connection] = () =>
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
    val socket = Net.connect(endpoint._2, endpoint._1)
    socket.onceConnect(() => p.trySuccess(socket): Unit)
    socket.onError(err => p.tryFailure(Exception(err.message)).pipe(_ => socket.destroy()))

  override def in(port: Port)(onReceive: Message => Unit): () => Future[ListenerRef] = () =>
    val listener = ServerSocketListener(onReceive)
    fromPromise[Listener]: p =>
      listener.serverSocket.onError: err =>
        p.tryFailure(Exception(err.message)): Unit
        listener.serverSocket.close()
      listener.serverSocket.listen(port)(() => p.trySuccess(listener): Unit)
    .map(ListenerRef(_, listener.acceptPromise.future))

  private class ServerSocketListener(onReceive: Message => Unit) extends Listener:
    private var open = true
    private val buffer = ArrayBuffer[Byte]()
    val acceptPromise: Promise[Unit] = Promise[Unit]()
    val serverSocket: Server = Net.createServer(socket => socket.onData(serve(_)(using socket)))

    private def serve(chunk: Uint8Array)(using socket: Socket): Unit =
      for i <- 0 until chunk.length do buffer += chunk(i).toByte
      continually(validate(readMessageLength))
        .takeWhile(msgLen => msgLen.isSuccess && buffer.length >= Integer.BYTES + msgLen.get)
        .collect { case Success(value) => value }
        .filter(_ > 0)
        .foreach: msgLen =>
          val msgBytes = buffer.slice(Integer.BYTES, Integer.BYTES + msgLen).toArray
          buffer.remove(0, Integer.BYTES + msgLen)
          onReceive(deserialize(msgBytes))

    private def readMessageLength: Try[Int] = Try(ByteBuffer.wrap(buffer.slice(0, Integer.BYTES).toArray).getInt)

    private def validate(msgLen: Try[Int])(using socket: Socket): Try[Int] = msgLen.flatMap: rawLen =>
      Try(rawLen ensuring (_ < conf.maxMessageSize)).recoverWith(Failure(_).tap(_ => socket.destroy()))

    override def boundPort: Port = serverSocket.address().port

    override def close(): Unit =
      open = false
      serverSocket.close()
      acceptPromise.trySuccess(()): Unit

    override def isOpen: Boolean = open
  end ServerSocketListener

  private def fromPromise[T](logic: Promise[T] => Unit): Future[T] = Promise[T].tap(logic).future
end SocketNetworking
