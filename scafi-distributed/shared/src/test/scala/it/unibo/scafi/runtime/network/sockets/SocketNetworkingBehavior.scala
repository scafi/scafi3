package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.CopyOnWriteArrayList

import scala.concurrent.{ ExecutionContext, Future }

import it.unibo.scafi.runtime.network.sockets.InetTypes.*
import it.unibo.scafi.message.Codables.forStringsInBinaryFormat

trait SocketNetworkingBehavior extends AsyncSpec:

  given SocketConfiguration = SocketConfiguration.basic

  type PlainTextNetworking = Networking & {
    type MessageIn = String
    type MessageOut = String
  }

  given PlainTextNetworking = new SocketNetworking:
    override type MessageIn = String
    override type MessageOut = String

  def anInboundConnectionListener(using net: Networking)(using ExecutionContext): Unit =
    it should "be able to be initialized on a free port" in:
      val server = net.in(FreePort)(nop)
      server.run() verify: ref =>
        ref.listener.isOpen shouldBe true
        ref.listener.close()
        ref.listener.isOpen shouldBe false

  def anOutboundConnection(using net: Networking): Unit =
    it should "fail to connect to an unavailable remote endpoint" in:
      val nonExistentServerPort: Port = 5050
      val client = net.out(Endpoint(Localhost, nonExistentServerPort))
      client.run().failed map:
        _ shouldBe a[Throwable]

    it should "be able to connect to an available remote endpoint" in:
      usingServer(nop): client =>
        client.isOpen shouldBe true
        client.close()
        client.isOpen shouldBe false

  def both(using net: PlainTextNetworking) =
    it should "be able to accept incoming connections from remote endpoints" in:
      val receivedMessages = CopyOnWriteArrayList[net.MessageIn]()
      val messages = Seq("Hello", "World", "Scafi", "Networking")
      usingServer(msg => receivedMessages.add(msg): Unit):
        _ send messages verifying eventually(receivedMessages should contain theSameElementsInOrderAs messages)

    it should "close connections with clients attempting to flood the server" in:
      val tooLargeMessage = "A" * 65_536
      usingServer(nop): client =>
        client send Seq(tooLargeMessage) verifying eventually(client.isOpen shouldBe false)

  private def usingServer[Result](using
      net: Networking,
  )(onMessage: net.MessageIn => Unit)(todo: net.Connection => Future[Result]) =
    for
      server <- net.in(FreePort)(onMessage).run()
      client <- net.out(Endpoint(Localhost, server.listener.boundPort)).run()
      result <- todo(client)
      _ = client.close()
      _ = server.listener.close()
    yield result

  extension (using net: Networking)(client: net.Connection)
    infix def send(messages: Seq[net.MessageOut]) =
      messages.foldLeft(Future.unit)((acc, msg) => acc.flatMap(_ => client.send(msg)))

  private val nop: Any => Unit = _ => ()

end SocketNetworkingBehavior
