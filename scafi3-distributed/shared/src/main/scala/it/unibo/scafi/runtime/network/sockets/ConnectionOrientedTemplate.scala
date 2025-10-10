package it.unibo.scafi.runtime.network.sockets

import java.nio.{ ByteBuffer, ByteOrder }

import scala.annotation.tailrec
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

import it.unibo.scafi.message.{ BinaryDecodable, BinaryEncodable }
import it.unibo.scafi.message.Codable.{ decode, encode }

/**
 * Template for connection-oriented networking components.
 * @param ec
 *   the execution context for handling asynchronous operations
 * @param conf
 *   the connection configuration settings
 */
trait ConnectionOrientedTemplate(using ec: ExecutionContext, conf: ConnectionConfiguration)
    extends ConnectionOrientedNetworking:

  override type MessageIn: BinaryDecodable

  override type MessageOut: BinaryEncodable

  /** An abstract connection template with pre-cooked `send` logic. */
  trait ConnectionTemplate extends Connection:
    override def send(msg: MessageOut): Future[Unit] =
      for
        serializedMsg <- Future(encode(msg))
        lengthBytes <- Future(serializedMsg.length.toBytes)
        data = lengthBytes ++ serializedMsg
        _ <- write(data)
      yield ()

    /**
     * Writes the given buffer over the underlying connection.
     * @param data
     *   the data to be sent.
     * @return
     *   a `Future` that completes when the data has been sent successfully, or fails with the reason of the failure.
     */
    def write(data: Array[Byte]): Future[Unit]

  /**
   * A connection listener template with pre-cooked message serving logic.
   * @tparam Client
   *   the client abstraction type.
   */
  trait ListenerTemplate[Client](onReceive: MessageIn => Unit) extends Listener:
    @tailrec
    final protected def serve(using client: Client): Try[Unit] = readMessageLength match
      case Success(None) => Success(())
      case Success(Some(0)) => serve
      case Success(Some(len)) if len.isValid =>
        val result = for
          rawMessage <- readMessage(len)
          message <- Try(decode(rawMessage))
          _ <- Try(onReceive(message))
        yield ()
        if result.isSuccess then serve else result
      case Success(_) => Failure(IllegalArgumentException("Message length validation failed"))
      case Failure(e) => Failure(e)

    extension (msgLen: Int) private def isValid: Boolean = msgLen < conf.maxMessageSize

    /**
     * Reads the length of the next message from the client socket, if available.
     * @param client
     *   the client socket to read from.
     * @return
     *   a `Success` containing a `Some[Int]` with the length of the next message, or `None` if the end of the stream is
     *   reached. A `Failure` is returned if an error occurs during reading.
     */
    def readMessageLength(using client: Client): Try[Option[Int]]

    /**
     * Reads a message of the specified length from the client socket.
     * @param length
     *   the length of the message to read.
     * @param client
     *   the client socket to read from.
     * @return
     *   a `Success` containing the message data as an array of bytes, or a `Failure` if an error occurs during reading.
     */
    def readMessage(length: Int)(using client: Client): Try[Array[Byte]]

    /**
     * @return
     *   the `Future` representing the async task accepting and handling incoming connections.
     * @see
     *   [[ListenerRef]]
     */
    def accept: Future[Unit]
  end ListenerTemplate

  extension (n: Int)
    /** @return the big-endian byte representation of this integer. */
    protected def toBytes: Array[Byte] =
      ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN).putInt(n).array()
end ConnectionOrientedTemplate
