package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.LazyList.continually
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Try, Using }
import scala.util.Using.Releasable

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
   * @tparam Client
   *   the [[Releasable]] client abstraction type.
   */
  trait ListenerTemplate[Client: Releasable](onReceive: MessageIn => Unit) extends Listener:
    protected def serve(using client: Client): Try[Unit] = Using(client): _ =>
      continually(validate(readMessageLength))
        .filter(_ > 0)
        .map(readMessage andThen decode)
        .foreach(onReceive)

    private def validate(msgLen: Int): Int = msgLen ensuring (_ < conf.maxMessageSize)

    /**
     * Reads the length of the next message from the client socket.
     * @param client
     *   the client socket to read from.
     * @return
     *   the length of the message
     */
    def readMessageLength(using client: Client): Int

    /**
     * Reads a message of the specified length from the client socket.
     * @param length
     *   the length of the message to read.
     * @param client
     *   the client socket to read from.
     * @return
     *   an `Array[Byte]` containing the message data.
     */
    def readMessage(length: Int)(using client: Client): Array[Byte]

    /**
     * @return
     *   the `Future` representing the async task accepting and handling incoming connections.
     * @see
     *   [[ListenerRef]]
     */
    def accept: Future[Unit]
  end ListenerTemplate
end ConnectionOrientedTemplate
