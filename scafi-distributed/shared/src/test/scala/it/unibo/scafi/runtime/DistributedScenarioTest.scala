package it.unibo.scafi.runtime

import java.nio.charset.StandardCharsets

import scala.concurrent.Future
import scala.concurrent.duration.{ DurationInt, FiniteDuration }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.runtime.network.sockets.{ AsyncSpec, SocketConfiguration, SocketNetworkManager }
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, FreePort, Localhost }
import it.unibo.scafi.test.environment.{ Environment, IntAggregateContext, Node }
import it.unibo.scafi.test.environment.Grids.vonNeumannGrid

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
      env = vonNeumannGrid(
        sizeX,
        sizeY,
        exchangeContextFactory[ID, SocketNetworkManager[ID]],
        socketNetworkFactory[Map[ID, Int], ExchangeAggregateContext[ID]].andThen(_.tap(networks.add)),
      )(probe.program)
      res <- eventually:
        env.cycleInOrder()
        env.status shouldBe probe.expected
      _ = networks.foreach(_.close())
    yield res

  private def socketNetworkFactory[Result, Context <: IntAggregateContext](using
      env: Environment[Result, Context, SocketNetworkManager[ID]],
  )(node: Node[Result, Context, SocketNetworkManager[ID]]): SocketNetworkManager[ID] =
    lazy val neighbors = env.nodes
      .filter(n => env.areConnected(env, n, node) && n != node)
      .map(n => n.id -> Endpoint(Localhost, n.networkManager.boundPort.get))
      .toMap
    val network = SocketNetworkManager.withFixedNeighbors(node.id, FreePort, neighbors)
    network.start().onComplete(result => require(result.isSuccess, s"Failed to start network: ${result.failed.get}"))
    network

end DistributedScenarioTest
