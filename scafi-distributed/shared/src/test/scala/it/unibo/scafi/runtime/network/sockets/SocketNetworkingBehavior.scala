package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.CopyOnWriteArrayList

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt

import it.unibo.scafi.runtime.network.sockets.InetTypes.*

trait SocketNetworkingBehavior extends NetworkingTest:

  type Message = String

  def anInboundConnectionListener(using networking: Networking[Message, Message])(using ExecutionContext): Unit =
    it should "be able to be initialized on a free port" in:
      val server = networking.in(FreePort)(nop)
      server.run() verify: ref =>
        ref.listener.isOpen shouldBe true
        ref.listener.close()
        ref.listener.isOpen shouldBe false

  def anOutboundConnection(using networking: Networking[Message, Message]): Unit =
    it should "be able to connect to an available remote endpoint" in:
      usingServer(nop): client =>
        client.isOpen shouldBe true
        client.close()
        client.isOpen shouldBe false

    it should "fail to connect to an unavailable remote endpoint" in:
      val nonExistentServerPort: Port = 5050
      val client = networking.out(Endpoint(Localhost, nonExistentServerPort))
      client.run().failed map:
        _ shouldBe a[Throwable]

  def both(using networking: Networking[Message, Message]) =
    it should "be able to accept incoming connections from remote endpoints" in:
      val receivedMessages = CopyOnWriteArrayList[String]()
      val messages = Seq("Hello", "World", "Scafi", "Networking")
      usingServer(msg => receivedMessages.add(msg): Unit):
        _ send messages verifying:
          eventually(timeout = 2.seconds, interval = 250.millis):
            receivedMessages should contain theSameElementsInOrderAs messages

    it should "close connections with clients attempting to flood the server" in:
      val tooLargeMessage = "A" * 65_536
      usingServer(nop): client =>
        client send Seq(tooLargeMessage) verifying:
          eventually(timeout = 500.millis, interval = 100.millis):
            client.isOpen shouldBe false
  end both

  private def usingServer[Result](using
      net: Networking[Message, Message],
  )(onMessage: Message => Unit)(todo: net.Connection => Future[Result]) =
    for
      server <- net.in(FreePort)(onMessage).run()
      client <- net.out(Endpoint(Localhost, server.listener.boundPort)).run()
      result <- todo(client)
      _ = client.close()
      _ = server.listener.close()
    yield result

  extension (using net: Networking[Message, Message])(client: net.Connection)
    infix def send(messages: Seq[Message]) =
      messages.foldLeft(Future.unit)((acc, msg) => acc.flatMap(_ => client.send(msg)))

  private val nop: Any => Unit = _ => ()

end SocketNetworkingBehavior
