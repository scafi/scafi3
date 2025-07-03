package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.scalajs.js.typedarray.{ ArrayBuffer, DataView, Uint8Array }

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*

trait SocketNetworking[Message: Serializable](using ec: ExecutionContext) extends Networking[Message, Message]:

  object Events:
    val connect = "connect"
    val error = "error"
    val listening = "listening"
    val data = "data"

  import Events.*

  override def out(endpoint: (String, Int)): () => Future[Connection] = () =>
    def createConnection(socket: Socket): Connection = new Connection:
      override def close(): Unit = socket.destroy()
      override def isOpen: Boolean = !socket.destroyed
      override def send(msg: Message): Future[Unit] =
        if !isOpen then Future.failed(IllegalStateException("Connection is closed!"))
        else
          val promise = Promise[Unit]()
          val bytes = serialize(msg)
          val buffer = new ArrayBuffer(4 + bytes.length)
          val view = new DataView(buffer)
          view.setUint32(0, bytes.length)
          val data = new Uint8Array(buffer)
          for i <- bytes.indices do data(i + 4) = bytes(i)
          socket.write(data)(err => if err eq null then promise.trySuccess(()) else promise.tryFailure(Exception(err)))
          promise.future
    val promisedConnection = Promise[Connection]()
    val socket = Net.connect(endpoint._2, endpoint._1)
    socket.on(connect)(_ => promisedConnection.trySuccess(createConnection(socket)): Unit)
    socket.on(error): err =>
      promisedConnection.tryFailure(Exception(err.asInstanceOf[scala.scalajs.js.Dynamic].message.toString)): Unit
      socket.destroy()
    promisedConnection.future

  override def in(port: Int)(onReceive: Message => Unit): () => Future[ListenerRef] = () =>
    val connListenerPromise = Promise[Listener]()
    val acceptPromise = Promise[Unit]()
    def react(data: Uint8Array): Unit =
      val view = new DataView(data.buffer, data.byteOffset, data.byteLength)
      if data.byteLength >= 4 then
        val length = view.getUint32(0) // body length
        if data.byteLength >= 4 + length then
          val bytes = new Array[Byte](data.length - 4)
          for i <- 0 until bytes.length do bytes(i) = data(i + 4).toByte
          onReceive(deserialize(bytes))
    val serverSocket = Net.createServer(_.on(data)(data => react(data.asInstanceOf[Uint8Array])))
    val listener = new Listener:
      private val open = AtomicBoolean(true)
      override def boundPort: Port = serverSocket.address().port
      override def close(): Unit =
        open.set(false)
        serverSocket.close()
        acceptPromise.trySuccess(()): Unit
      override def isOpen: Boolean = open.get()
    serverSocket.on(listening)(_ => connListenerPromise.trySuccess(listener): Unit)
    serverSocket.on(error): err =>
      connListenerPromise.tryFailure(Exception(err.asInstanceOf[scala.scalajs.js.Dynamic].message.toString)): Unit
      serverSocket.close()
    serverSocket.listen(port)
    connListenerPromise.future.map(ListenerRef(_, acceptPromise.future))
end SocketNetworking
