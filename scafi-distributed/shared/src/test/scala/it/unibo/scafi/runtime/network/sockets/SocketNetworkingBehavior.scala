package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

trait SocketNetworkingBehavior:
  this: NetworkingTest =>

  given ExecutionContext = compiletime.deferred

  private val FreePort = 0

  def anInboundConnectionListener(networking: => Networking[String, String] & InetTypes) =
    it should "be initializable on a specific port" in:
      val connectionListener = networking.in(FreePort)(_ => ())
      whenReady(connectionListener): conn =>
        conn.isOpen shouldBe true
        conn.close()
        conn.isOpen shouldBe false
