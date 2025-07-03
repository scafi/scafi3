package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.CopyOnWriteArrayList

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt

import it.unibo.scafi.runtime.network.sockets.InetTypes.localhost

trait SocketNetworkingBehavior extends NetworkingTest:

  def anInboundConnectionListener(networking: => Networking[String, String])(using ExecutionContext): Unit =
    it should "be able to be initialized on a free port" in:
      val server = networking.in(FreePort)(nop)
      server() map: ref =>
        ref.listener.isOpen shouldBe true
        ref.listener.close()
        ref.listener.isOpen shouldBe false

  def anOutboundConnection(networking: => Networking[String, String]): Unit =
    it should "be able to connect to an available remote endpoint" in:
      for
        server <- networking.in(FreePort)(nop)()
        client <- networking.out(endpoint = (localhost, server.listener.boundPort))()
      yield
        client.isOpen shouldBe true
        client.close()
        client.isOpen shouldBe false

    it should "fail to connect to an unavailable remote endpoint" in:
      val nonExistentServerPort = 5050
      val client = networking.out(endpoint = (localhost, nonExistentServerPort))
      client().failed map:
        _ shouldBe a[Throwable]
        // ex.getMessage should (include("Connection refused") or include("Could not connect to address"))

  def both(networking: => Networking[String, String]) =
    it should "be able to accept incoming connections from remote endpoints" in:
      val receivedMessages = CopyOnWriteArrayList[String]()
      val messages = Seq("Hello", "World", "Scafi", "Networking")
      for
        server <- networking.in(FreePort)(msg => receivedMessages.add(msg): Unit)()
        client <- networking.out((localhost, server.listener.boundPort))()
        _ <- messages.foldLeft(Future.unit)((acc, msg) => acc.flatMap(_ => client.send(msg)))
        assertion <- eventually(timeout = 2.seconds):
          receivedMessages.forEach(println)
          receivedMessages should contain theSameElementsInOrderAs messages
      yield assertion

  private val FreePort = 0
  private val nop: Any => Unit = _ => ()
end SocketNetworkingBehavior
