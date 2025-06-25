package it.unibo.scafi.runtime.network

trait Serializable[T]:

  def serialize(t: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T

object Serializable:
  def serialize[T](t: T)(using s: Serializable[T]): Array[Byte] = s.serialize(t)
  def deserialize[T](bytes: Array[Byte])(using s: Serializable[T]): T = s.deserialize(bytes)

  given Serializable[String] with
    def serialize(t: String): Array[Byte] = t.getBytes
    def deserialize(bytes: Array[Byte]): String = String(bytes)
