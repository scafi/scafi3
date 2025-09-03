package it.unibo.scafi.message

/**
 * A type class for encoding messages.
 * @tparam From
 *   the type of the message to encode.
 * @tparam To
 *   the type of the encoded message.
 */
trait Encodable[-From, +To]:

  /**
   * Encodes the given value into the target format.
   * @param value
   *   the value to encode.
   * @return
   *   the encoded value in the target type.
   */
  def encode(value: From): To

object Encodable:

  /**
   * Encodes the given value into the target format using a contextually available `Encodable` instance.
   * @param value
   *   the value to encode.
   * @param e
   *   the contextually available `Encodable` instance for the value type.
   * @tparam From
   *   the type of the value to encode.
   * @tparam To
   *   the type of the encoded value.
   * @return
   *   the encoded value in the target type.
   */
  def encode[From, To](value: From)(using e: Encodable[From, To]): To = e.encode(value)

/** Type alias for [[Encodable]] that enables using `Encodable[Message, Format]` as a context bound. */
type EncodableTo[Format] = [Message] =>> Encodable[Message, Format]

/** Type alias for a binary [[Encodable]], namely one that encodes messages into raw bytes. */
type BinaryEncodable[Message] = Encodable[Message, Array[Byte]]

/**
 * A type class for decoding messages.
 * @tparam From
 *   the type of the data to decode.
 * @tparam To
 *   the type of the decoded data.
 */
trait Decodable[-From, +To]:

  /**
   * Decodes the given data into the target message type.
   * @param data
   *   the data to decode.
   * @return
   *   the decoded data in the target type.
   */
  def decode(data: From): To

object Decodable:

  /**
   * Decodes the given data into the target message type using a contextually available `Decodable` instance.
   * @param data
   *   the data to decode.
   * @param d
   *   the contextually available `Decodable` instance for the data type.
   * @tparam From
   *   the type of the data to decode.
   * @tparam To
   *   the type of the decoded message.
   * @return
   *   the decoded data in the target type.
   */
  def decode[From, To](data: From)(using d: Decodable[From, To]): To = d.decode(data)

/** Type alias for [[Decodable]] that enables using `Decodable[Format, Message]` as a context bound. */
type DecodableFrom[Format] = [Message] =>> Decodable[Format, Message]

/** Type alias for binary [[Decodable]], namely one that decodes messages from raw bytes. */
type BinaryDecodable[Message] = Decodable[Array[Byte], Message]

/**
 * A type class for encoding and decoding messages.
 * @tparam Message
 *   the type of messages to encode.
 * @tparam Format
 *   the type of the encoded format.
 */
trait Codable[Message, Format] extends Encodable[Message, Format] with Decodable[Format, Message]

object Codable:
  export Encodable.*
  export Decodable.*

/** A type alias for [[Codable]] that enables using `Codable[Message, Format]` as a context bound. */
type CodableFromTo[Format] = [Message] =>> Codable[Message, Format]

/** Type alias for a binary [[Codable]], namely one that encodes messages to bytes and decodes them back. */
type BinaryCodable[Message] = Codable[Message, Array[Byte]]

object Codables:
  import java.nio.charset.StandardCharsets

  /**
   * A [[Codable]] that does not perform any transformation on the messages, leaving them as-is. This is useful in
   * non-distributed environments, like simulations or local testing, where messages are simply passed around without
   * any form of (de)serialization.
   * @tparam Message
   *   the type of the message.
   * @return
   *   a [[Codable]] instance that encodes and decodes messages leaving them unchanged.
   */
  given forInMemoryCommunications[Message]: Codable[Message, Message] with
    inline def encode(msg: Message): Message = msg
    inline def decode(msg: Message): Message = msg

  /** @return a [[BinaryCodable]] for encoding and decoding stringified messages in binary format. */
  given forStringsInBinaryFormat: Codable[String, Array[Byte]] with
    def encode(msg: String): Array[Byte] = msg.getBytes(StandardCharsets.UTF_8)
    def decode(bytes: Array[Byte]): String = new String(bytes, StandardCharsets.UTF_8)
end Codables
