package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

import it.unibo.scafi.runtime.network.sockets.InetTypes.localhost

import org.scalatest.time.{ Seconds, Span }

trait SocketNetworkingBehavior:
  this: NetworkingTest =>

  given ExecutionContext = compiletime.deferred

  def anInboundConnectionListener(networking: => Networking[String, String]) =
    it should "be initializable on a specific port" in:
      val server = networking.in(FreePort)(nop)
      whenReady(server()): connRef =>
        val listener = connRef.listener
        listener.isOpen shouldBe true
        listener.close()
        listener.isOpen shouldBe false
        eventually(connRef.accept.isCompleted shouldBe true)

  def anOutboundConnection(networking: => Networking[String, String]) =
    it should "be able to connect to an available remote endpoint" in:
      val server = networking.in(FreePort)(nop)
      whenReady(server()): connRef =>
        val listener = connRef.listener
        val client = networking.out(endpoint = (localhost, listener.boundPort))
        whenReady(client()): conn =>
          conn.isOpen shouldBe true
          conn.close()
          conn.isOpen shouldBe false

    it should "fail to connect to an unavailable remote endpoint" in:
      val port = 5050
      val client = networking.out(endpoint = (localhost, port))
      whenReady(client().failed): ex =>
        ex shouldBe a[Throwable]
        ex.getMessage should (
          include("Connection refused") // Native
            or include("Could not connect to address") // JVM
        )
  end anOutboundConnection

  def both(networking: => Networking[String, String]) =
    it should "be able to accept incoming connections from remote endpoints" in:
      given PatienceConfig = PatienceConfig(timeout = Span(1, Seconds))
      val receivedMessages = scala.collection.mutable.ListBuffer.empty[String]
      val msg = "Hello, Scafi!"
      val exchange = for
        server <- networking.in(FreePort)(receivedMessages.addOne)()
        client <- networking.out((localhost, server.listener.boundPort))()
        _ <- client.send(msg)
      yield (server, client)
      whenReady(exchange): (server, client) =>
        eventually(receivedMessages should contain theSameElementsAs Seq(msg))
        server.listener.close()
        client.close()

  private val FreePort = 0
  private val nop: Any => Unit = _ => ()
end SocketNetworkingBehavior
