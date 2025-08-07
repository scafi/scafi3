package it.unibo.scafi.runtime.network

/**
 * A type class for encoding messages.
 * @tparam Message
 *   the type of messages to be encoded.
 */
trait Encodable[Message]:

  /**
   * Encode a message into an array of bytes.
   * @param message
   *   the message to encode.
   * @return
   *   an array of bytes representing the encoded message.
   */
  def encode(message: Message): Array[Byte]

/**
 * A type class for decoding messages.
 * @tparam Message
 *   the type of messages to be decoded.
 */
trait Decodable[Message]:

  /**
   * Decode an array of bytes into a message.
   * @param bytes
   *   the array of bytes to decode.
   * @return
   *   the decoded message.
   */
  def decode(bytes: Array[Byte]): Message

/**
 * A type class that combines both encoding and decoding capabilities.
 */
trait Codable[Message] extends Encodable[Message] with Decodable[Message]

object Codable:

  /**
   * Encode the given message into an array of bytes using a contextually available `Encodable` instance.
   * @param message
   *   the message to encode.
   * @param e
   *   the contextually available `Encodable` instance for the message type.
   * @tparam Message
   *   the type of the message to encode.
   * @return
   *   an array of bytes representing the encoded message.
   */
  def encode[Message](message: Message)(using e: Encodable[Message]): Array[Byte] = e.encode(message)

  /**
   * Decode the given array of bytes into a message.
   * @param bytes
   *   the array of bytes to decode.
   * @param d
   *   the contextually available `Decodable` instance for the message type.
   * @tparam Message
   *   the type of the message to decode.
   * @return
   *   the decoded message.
   */
  def decode[Message](bytes: Array[Byte])(using d: Decodable[Message]): Message = d.decode(bytes)

  /** A default `Codable` instance for `String` messages. */
  given Codable[String] with
    def encode(t: String): Array[Byte] = t.getBytes
    def decode(bytes: Array[Byte]): String = String(bytes)

end Codable
