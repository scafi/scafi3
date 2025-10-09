package it.unibo.scafi.utils

import it.unibo.scafi.runtime.network.sockets.InetTypes.{ FreePort, Port }

import io.github.iltotore.iron.refineUnsafe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

object FreePortFinder:
  import java.net.ServerSocket

  /** @return a pool of free ports on the local machine with the requested [[size]]. */
  def get(size: Int): Seq[Port] =
    val sockets = (1 to size).map(_ => new ServerSocket(FreePort))
    try
      sockets.map: s =>
        s.setReuseAddress(true) // allow quick reuse of the port after the socket is closed
        s.getLocalPort.refineUnsafe
    finally sockets.foreach(_.close())

class FreePortFinderTest extends AnyFlatSpec with should.Matchers:

  "FreePortFinder" should "return the requested number of free ports" in:
    val freePorts = FreePortFinder.get(5)
    freePorts.size shouldBe 5
