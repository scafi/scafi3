package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

import it.unibo.scafi.runtime.network.sockets.InetTypes.localhost

import org.scalatest.time.{ Seconds, Span }

trait SocketNetworkingBehavior:
  this: NetworkingTest =>

  given ExecutionContext = compiletime.deferred

  def anInboundConnectionListener(networking: => Networking[String, String] & InetTypes) =
    it should "be initializable on a specific port" in:
      val server = networking.in(FreePort)(nop)
      whenReady(server): conn =>
        conn.isOpen shouldBe true
        conn.close()
        conn.isOpen shouldBe false

  def anOutboundConnection(networking: => Networking[String, String] & InetTypes) =
    it should "be able to connect to an available remote endpoint" in:
      whenReady(networking.in(FreePort)(nop)): server =>
        val client = networking.out(endpoint = (localhost, server.boundPort))
        whenReady(client): conn =>
          conn.isOpen shouldBe true
          conn.close()
          conn.isOpen shouldBe false

    it should "fail to connect to an unavailable remote endpoint" in:
      val port = 5050
      whenReady(networking.out(endpoint = (localhost, port)).failed): ex =>
        ex shouldBe a[Throwable]
        ex.getMessage should (
          include("Connection refused") // Native
            or include("Could not connect to address") // JVM
        )
  end anOutboundConnection

  def both(networking: => Networking[String, String] & InetTypes) =
    it should "be able to accept incoming connections from remote endpoints" in:
      given PatienceConfig = PatienceConfig(timeout = Span(1, Seconds))
      val receivedMessages = scala.collection.mutable.ListBuffer.empty[String]
      val msg = "Hello, Scafi!"
      val exchange = for
        server <- networking.in(FreePort)(receivedMessages.addOne)
        client <- networking.out((localhost, server.boundPort))
        _ <- client.send(msg)
      yield (server, client)
      whenReady(exchange): (server, client) =>
        eventually(receivedMessages should contain theSameElementsAs Seq(msg))
        server.close()
        client.close()

  private val FreePort = 0
  private val nop: Any => Unit = _ => ()
end SocketNetworkingBehavior
