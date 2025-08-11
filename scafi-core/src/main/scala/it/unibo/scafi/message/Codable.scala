package it.unibo.scafi.message

trait Encodable[-From, +To]:
  def encode(value: From): To

object Encodable:
  def encode[From, To](value: From)(using e: Encodable[From, To]): To = e.encode(value)

type EncodableTo[Format] = [Message] =>> Encodable[Message, Format]

type BinaryEncodable[Message] = Encodable[Message, Array[Byte]]

trait Decodable[-From, +To]:
  def decode(bytes: From): To

object Decodable:
  def decode[From, To](bytes: From)(using d: Decodable[From, To]): To = d.decode(bytes)

type DecodableFrom[Format] = [Message] =>> Decodable[Format, Message]

type BinaryDecodable[Message] = Decodable[Array[Byte], Message]

trait Codable[Message, Format] extends Encodable[Message, Format] with Decodable[Format, Message]

object Codable:
  export Encodable.*
  export Decodable.*

type CodableFromTo[Format] = [Message] =>> Encodable[Message, Format] & Decodable[Format, Message]

type BinaryCodable[Message] = Codable[Message, Array[Byte]]

object Codables:
  import java.nio.charset.StandardCharsets

  given inMemory[Message]: Codable[Message, Message] = new Codable[Message, Message]:
    inline def encode(msg: Message): Message = msg
    inline def decode(msg: Message): Message = msg

  given forStringsInBinaryFormat: Codable[String, Array[Byte]] = new Codable[String, Array[Byte]]:
    inline def encode(msg: String): Array[Byte] = msg.getBytes(StandardCharsets.UTF_8)
    inline def decode(bytes: Array[Byte]): String = new String(bytes, StandardCharsets.UTF_8)
