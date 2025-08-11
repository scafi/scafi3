package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.LazyList.continually
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }
import scala.util.Using.Releasable

import it.unibo.scafi.message.{ BinaryDecodable, BinaryEncodable }
import it.unibo.scafi.message.Codable.*

trait NetworkingTemplate(using ec: ExecutionContext, conf: SocketConfiguration) extends Networking:

  override type MessageIn: BinaryDecodable

  override type MessageOut: BinaryEncodable

  /** An abstract connection template with pre-cooked `send` logic. */
  trait ConnectionTemplate extends Connection:
    override def send(msg: MessageOut): Future[Unit] =
      for
        serializedMsg <- Future(encode(msg))
        lengthBytes <- Future(ByteBuffer.allocate(Integer.BYTES).putInt(serializedMsg.length).array())
        data = lengthBytes ++ serializedMsg
        _ <- write(data)
      yield ()

    /**
     * Writes the given buffer over the underlying connection.
     * @param buffer
     *   the data to be sent.
     * @return
     *   a `Future` that completes when the data has been sent successfully, or fails with the reason of the failure.
     */
    def write(buffer: Array[Byte]): Future[Unit]

  /**
   * A connection listener template with pre-cooked message serving logic.
   * @tparam Socket
   *   the [[Releasable]] client socket type.
   */
  trait ListenerTemplate[Socket: Releasable](onReceive: MessageIn => Unit) extends Listener:
    protected def serve(using Socket) = continually(validate(readMessageLength))
      .takeWhile(_.isSuccess)
      .collect { case Success(value) => value }
      .filter(_ > 0)
      .map(readMessage andThen decode)
      .foreach(onReceive)

    private def validate(msgLen: Try[Int])(using client: Socket): Try[Int] = msgLen.flatMap: rawLen =>
      Try(rawLen ensuring (_ < conf.maxMessageSize)).recoverWith: err =>
        Socket.release(client)
        Failure(err)

    /**
     * Reads the length of the next message from the client socket.
     * @param client
     *   the client socket to read from.
     * @return
     *   a `Try` containing the length of the message, or a failure if reading fails.
     */
    def readMessageLength(using client: Socket): Try[Int]

    /**
     * Reads a message of the specified length from the client socket.
     * @param length
     *   the length of the message to read.
     * @param client
     *   the client socket to read from.
     * @return
     *   an `Array[Byte]` containing the message data.
     */
    def readMessage(length: Int)(using client: Socket): Array[Byte]

    /**
     * @return
     *   the `Future` representing the async task accepting and handling incoming connections.
     * @see
     *   [[ListenerRef]]
     */
    def accept: Future[Unit]
  end ListenerTemplate
end NetworkingTemplate
