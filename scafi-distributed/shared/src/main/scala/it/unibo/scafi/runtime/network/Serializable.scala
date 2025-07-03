package it.unibo.scafi.runtime.network

/**
 * A type class for serializing and deserializing messages.
 * @tparam Message
 *   the type of messages to serialize and deserialize.
 */
trait Serializable[Message]:

  /**
   * Serializes a message into an array of bytes.
   * @param message
   *   the message to serialize.
   * @return
   *   an array of bytes representing the serialized message.
   */
  def serialize(message: Message): Array[Byte]

  /**
   * Deserializes an array of bytes into a message.
   * @param bytes
   *   the array of bytes to deserialize.
   * @return
   *   the deserialized message.
   */
  def deserialize(bytes: Array[Byte]): Message

object Serializable:

  /**
   * Serializes the given message into an array of bytes using a contextually available `Serializable` instance.
   * @param message
   *   the message to serialize.
   * @param s
   *   the contextually available `Serializable` instance for the message type.
   * @tparam Message
   *   the type of the message to serialize.
   * @return
   *   an array of bytes representing the serialized message.
   */
  def serialize[Message](message: Message)(using s: Serializable[Message]): Array[Byte] = s.serialize(message)

  /**
   * Deserializes the given array of bytes into a message.
   * @param bytes
   *   the array of bytes to deserialize.
   * @param s
   *   the contextually available `Serializable` instance for the message type.
   * @tparam Message
   *   the type of the message to deserialize.
   * @return
   *   the deserialized message.
   */
  def deserialize[Message](bytes: Array[Byte])(using s: Serializable[Message]): Message = s.deserialize(bytes)

  /** A default `Serializable` instance for `String` messages. */
  given Serializable[String] with
    def serialize(t: String): Array[Byte] = t.getBytes
    def deserialize(bytes: Array[Byte]): String = String(bytes)

end Serializable
