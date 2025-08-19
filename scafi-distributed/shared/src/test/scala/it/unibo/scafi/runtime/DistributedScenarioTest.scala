package it.unibo.scafi.runtime

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.Future
import scala.concurrent.duration.{ DurationInt, FiniteDuration }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.runtime.network.sockets.{ AsyncSpec, SocketConfiguration, SocketNetworkManager }
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, Localhost, Port }
import it.unibo.scafi.test.environment.{ Environment, Node }
import it.unibo.scafi.test.environment.Grids.{ vonNeumannGrid, IntAggregateContext }
import it.unibo.scafi.utils.{ Platform, PlatformRuntime }

import io.github.iltotore.iron.refineUnsafe
import org.scalatest.compatible.Assertion
import org.scalatest.time.{ Seconds, Span }

trait DistributedScenarioTest extends AsyncSpec with Programs:

  override given BinaryCodable[ID] = new BinaryCodable[ID]:
    def encode(msg: ID): Array[Byte] = msg.toString.getBytes(StandardCharsets.UTF_8)
    def decode(bytes: Array[Byte]): ID = new String(bytes, StandardCharsets.UTF_8).toInt

  given SocketConfiguration = new SocketConfiguration:
    override val inactivityTimeout: FiniteDuration = 2.seconds
    override val maxMessageSize: Int = 65_536

  def socketBasedDistributedEnvironment(probe: ProgramWithResult[Map[ID, Int]]): Future[Assertion] =
    given PatienceConfig = PatienceConfig(timeout = Span(10, Seconds), interval = Span(1, Seconds))
    for
      networks = collection.mutable.Set.empty[SocketNetworkManager[ID]]
      sizeX = 2
      sizeY = 2
      endpoints = (0 to sizeX).flatMap(x => (0 to sizeY).map(y => x + y -> Endpoint(Localhost, PortsPool.get))).toMap
      env = vonNeumannGrid(
        sizeX,
        sizeY,
        exchangeContextFactory,
        socketNetworkFactory[Map[ID, Int], ExchangeAggregateContext[ID]](endpoints).andThen(_.tap(networks.add)),
      )(probe.program)
      res <- eventually:
        env.cycleInOrder()
        env.status shouldBe probe.expected
      _ = networks.foreach(_.close())
    yield res

  private def socketNetworkFactory[R, Context <: IntAggregateContext](endpoints: Map[ID, Endpoint])(using
      env: Environment[R, Context],
  )(node: Node[R, Context]): SocketNetworkManager[ID] =
    val neighbors = env.nodes
      .filter(n => env.areConnected(env, n, node) && n != node)
      .map(n => n.id -> endpoints(n.id))
      .toMap
    val network = SocketNetworkManager.withFixedNeighbors(node.id, endpoints(node.id).port, neighbors)
    network.start().onComplete(result => require(result.isSuccess, s"Failed to start network: ${result.failed.get}"))
    network

  /**
   * Manages the assignment of TCP ports to nodes in a distributed setup using a pool of ports.
   * @note
   *   This is needed because tests of different platforms may run concurrently causing port conflicts. Moreover, each
   *   test can select a separate port to avoid issues with ports that are not immediately freed by the OS, incurring in
   *   binding exceptions.
   */
  object PortsPool:
    private val basePort = Platform.runtime match
      case PlatformRuntime.Jvm => 5060 to 5160
      case PlatformRuntime.Js => 5070 to 5170
      case PlatformRuntime.Native => 5080 to 5180
    private val current = AtomicInteger(0)

    def get: Port = basePort(current.getAndIncrement() % basePort.size).refineUnsafe

end DistributedScenarioTest
