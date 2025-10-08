package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.CopyOnWriteArrayList

import scala.concurrent.Future

import it.unibo.scafi.message.Codables.forStringsInBinaryFormat
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ autoRefine, Endpoint, FreePort, Localhost, Port }
import it.unibo.scafi.test.AsyncSpec

import org.scalatest.time.{ Millis, Seconds, Span }

trait SocketNetworkingBehavior extends AsyncSpec:

  given ConnectionConfiguration = ConnectionConfiguration.basic

  given PatienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(250, Millis))

  type PlainTextNetworking = ConnectionOrientedNetworking & {
    type MessageIn = String
    type MessageOut = String
  }

  given PlainTextNetworking = new SocketNetworking:
    override type MessageIn = String
    override type MessageOut = String

  def anInboundConnectionListener(using net: ConnectionOrientedNetworking): Unit =
    it should "be able to be initialized on a free port" in:
      val server = net.in(FreePort)(nop)
      server verify: ref =>
        ref.listener.isOpen shouldBe true
        ref.listener.close()
        ref.listener.isOpen shouldBe false

  def anOutboundConnection(using net: ConnectionOrientedNetworking): Unit =
    it should "fail to connect to an unavailable remote endpoint" in:
      val nonExistentServerPort: Port = 5050
      val client = net.out(Endpoint(Localhost, nonExistentServerPort))
      client.failed verify:
        _ shouldBe a[Throwable]

    it should "be able to connect to an available remote endpoint" in:
      usingServer(nop): client =>
        client.isOpen shouldBe true
        client.close()
        client.isOpen shouldBe false

  def both(using net: PlainTextNetworking): Unit =
    it should "be able to accept incoming connections from remote endpoints" in:
      val receivedMessages = CopyOnWriteArrayList[net.MessageIn]()
      val messages = Seq("Hello", "World", "Scafi", "Networking")
      usingServer(msg => receivedMessages.add(msg): Unit): client =>
        client send messages verifying eventually(receivedMessages should contain theSameElementsInOrderAs messages)

    it should "close connections with clients attempting to flood the server" in:
      val tooLargeMessage = "A" * 65_536
      usingServer(nop): client =>
        client send Seq(tooLargeMessage) verifying eventually(client.isOpen shouldBe false)

  private def usingServer[Result](using
      net: ConnectionOrientedNetworking,
  )(onMessage: net.MessageIn => Unit)(todo: net.Connection => Future[Result]) =
    for
      server <- net.in(FreePort)(onMessage)
      client <- net.out(Endpoint(Localhost, server.listener.boundPort))
      result <- todo(client)
      _ = client.close()
      _ = server.listener.close()
    yield result

  extension (using net: ConnectionOrientedNetworking)(client: net.Connection)
    infix private def send(messages: Seq[net.MessageOut]) =
      messages.foldLeft(Future.unit)((acc, msg) => acc.flatMap(_ => client.send(msg)))

  private val nop: Any => Unit = _ => ()

end SocketNetworkingBehavior
