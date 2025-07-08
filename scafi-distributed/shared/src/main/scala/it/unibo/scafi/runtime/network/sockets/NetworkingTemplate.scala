package it.unibo.scafi.runtime.network.sockets

import java.nio.ByteBuffer

import scala.concurrent.Future

import it.unibo.scafi.runtime.network.Serializable
import it.unibo.scafi.runtime.network.Serializable.*

trait NetworkingTemplate[Message: Serializable] extends Networking[Message, Message]:

  trait ConnectionTemplate extends Connection:
    override def send(msg: Message): Future[Unit] =
      val serializedMsg = serialize(msg)
      val lengthBytes = ByteBuffer.allocate(Integer.BYTES).putInt(serializedMsg.length).array()
      val data = lengthBytes ++ serializedMsg
      write(data)

    def write(buffer: Array[Byte]): Future[Unit]
