package it.unibo.scafi.runtime

import java.nio.charset.StandardCharsets

import scala.concurrent.Future
import scala.concurrent.duration.{ DurationInt, FiniteDuration }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.runtime.network.ExpirationConfiguration
import it.unibo.scafi.runtime.network.sockets.{
  ConnectionConfiguration,
  ConnectionOrientedNetworkManager,
  SocketNetworkManager,
}
import it.unibo.scafi.runtime.network.sockets.InetTypes.{ Endpoint, FreePort, Localhost }
import it.unibo.scafi.test.AsyncSpec
import it.unibo.scafi.test.environment.{ Environment, IntAggregateContext, Node }
import it.unibo.scafi.test.environment.Grids.vonNeumannGrid

import org.scalatest.compatible.Assertion
import org.scalatest.time.{ Seconds, Span }

trait DistributedScenarioTest extends AsyncSpec with Programs:

  given PatienceConfig = PatienceConfig(timeout = Span(15, Seconds), interval = Span(1, Seconds))

  override given BinaryCodable[ID] = new BinaryCodable[ID]:
    def encode(msg: ID): Array[Byte] = msg.toString.getBytes(StandardCharsets.UTF_8)
    def decode(bytes: Array[Byte]): ID = new String(bytes, StandardCharsets.UTF_8).toInt

  given ConnectionConfiguration = new ConnectionConfiguration:
    override val inactivityTimeout: FiniteDuration = 2.seconds
    override val maxMessageSize: Int = 65_536

  given ExpirationConfiguration = ExpirationConfiguration.basic

  "Evolve program" should "make single nodes evolve as expected" in
    socketBasedDistributedEnvironment(evolveProgram)

  "Neighbors discovery program" should "spread local values to neighborhood" in
    socketBasedDistributedEnvironment(neighborsDiscoveryProgram)

  "Exchange aggregate program with branch restriction" should "correctly spread local values to aligned neighbors" in
    socketBasedDistributedEnvironment(exchangeWithRestrictionsProgram)

  def socketBasedDistributedEnvironment[Result](probe: ProgramWithResult[Result]): Future[Assertion] =
    for
      networks = collection.mutable.Set.empty[ConnectionOrientedNetworkManager[ID]]
      env = vonNeumannGrid(
        sizeX = 2,
        sizeY = 2,
        exchangeContextFactory[ID, ConnectionOrientedNetworkManager[ID]],
        socketNetworkFactory.andThen(_.tap(networks.add)),
      )(probe.program)
      _ <- Future.sequence(env.nodes.map(_.networkManager.start()))
      res <- eventually:
        env.cycleInOrder()
        env.status shouldBe probe.expected
      _ = networks.foreach(_.close())
    yield res

  private def socketNetworkFactory[Result, Context <: IntAggregateContext](using
      env: Environment[Result, Context, ConnectionOrientedNetworkManager[ID]],
  )(node: Node[Result, Context, ConnectionOrientedNetworkManager[ID]]): ConnectionOrientedNetworkManager[ID] =
    lazy val neighbors = env.nodes
      .filter(n => env.areConnected(env, n, node) && n != node)
      .map(n => n.id -> Endpoint(Localhost, n.networkManager.boundPort.get))
      .toMap
    SocketNetworkManager.withFixedNeighbors(node.id, FreePort, neighbors)

end DistributedScenarioTest
