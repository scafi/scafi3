package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

trait SocketNetworkingBehavior:
  this: NetworkingTest =>

  given ExecutionContext = compiletime.deferred

  def anInboundConnectionListener(networking: => Networking[String, String] & InetTypes) =
    it should "be initializable on a specific port" in:
      val connectionListener = networking.in(port = 5050)(_ => ())
      whenReady(connectionListener): conn =>
        conn.isOpen shouldBe true
        conn.close()
        conn.isOpen shouldBe false
