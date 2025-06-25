package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext
import scala.util.{ Success, Try }

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.Inside

trait SocketNetworkingTest extends AnyWordSpec with should.Matchers with ScalaFutures with Eventually with Inside:

  val networking: Networking[String, String] & InetTypes

  given ExecutionContext = compiletime.deferred

  def tests() =
    "SocketNetworking" should:
      "allow initializing an inbound socket connection on a specific port" in:
        val connectionListener = networking.in(port = 5_000)(_ => ())
        eventually:
          connectionListener.isCompleted shouldBe true
          inside(connectionListener.value):
            case Some(Success(conn)) => conn.isOpen shouldBe true
