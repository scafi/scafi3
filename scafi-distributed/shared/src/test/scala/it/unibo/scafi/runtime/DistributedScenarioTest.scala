package it.unibo.scafi.runtime

import java.nio.charset.StandardCharsets

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

import it.unibo.scafi.runtime.network.sockets.AsyncSpec
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.test.environment.Grids.vonNeumannGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, Localhost }
import it.unibo.scafi.runtime.network.sockets.{ SocketBasedNetworkManager, SocketConfiguration }
import it.unibo.scafi.utils.{ Platform, PlatformRuntime }

import io.github.iltotore.iron.refineUnsafe
import org.scalatest.time.{ Seconds, Span }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait DistributedScenarioTest extends AsyncSpec with Programs:

  override given BinaryCodable[ID] = new BinaryCodable[ID]:
    def encode(msg: ID): Array[Byte] = msg.toString.getBytes(StandardCharsets.UTF_8)
    def decode(bytes: Array[Byte]): ID = new String(bytes, StandardCharsets.UTF_8).toInt

  given SocketConfiguration = new SocketConfiguration:
    override val inactivityTimeout: FiniteDuration = 2.seconds
    override val maxMessageSize: Int = 65_536

  def expected() =
    it should "successfully execute in a distributed scenario with socket-based network manager" in:
      scribe.info("==== STARTING DISTRIBUTED SCENARIO TEST ====")
      given PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(2, Seconds))
      var networks = Set.empty[SocketBasedNetworkManager[ID]]
      val initialPort = Platform.runtime match
        case PlatformRuntime.Jvm => 5060
        case PlatformRuntime.Js => 5070
        case PlatformRuntime.Native => 5080
      val env = vonNeumannGrid(
        sizeX = 2,
        sizeY = 2,
        contextFactory = exchangeContextFactory,
        networkFactory = env ?=>
          node =>
            val neighbors = env.nodes
              .filter(n => env.areConnected(env, n, node) && n != node)
              .map(n => n.id -> Endpoint(Localhost, (initialPort + n.id).refineUnsafe))
              .toMap
            val network = SocketBasedNetworkManager
              .withStaticallyAssignedNeighbors(node.id, initialPort + node.id, neighbors)
            networks = networks + network
            network,
      )(exchangeWithRestrictions)
      val expectedResult = Map(
        0 -> Map(2 -> 100),
        1 -> Map(3 -> 200),
        2 -> Map(0 -> 100),
        3 -> Map(1 -> 200),
      )
      eventually:
        scribe.info("Cycling in order!")
        env.cycleInOrder()
        val status = env.status.asInstanceOf[Map[ID, Map[ID, Int]]]
        scribe.info(s"Current status is: $status")
        status shouldBe expectedResult
      .map: res => // Ensure the network is closed after the test
        networks.foreach(_.close())
        res

end DistributedScenarioTest
