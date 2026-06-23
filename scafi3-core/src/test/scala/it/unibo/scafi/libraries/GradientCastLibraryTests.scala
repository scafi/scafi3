package it.unibo.scafi.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.All.{ distanceTo, localId, neighborValues }
import it.unibo.scafi.libraries.GradientCastLibrary.{
  broadcast,
  channel,
  distanceBetween,
  gradientCast,
}
import it.unibo.scafi.message.{ Codable, Codables }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager
import it.unibo.scafi.utils.boundaries.CommonBoundaries.given

import org.scalatest.Inspectors

class GradientCastLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type TestCtx = ExchangeAggregateContext[Int]

  // ── single-device (isolated) tests ───────────────────────────────────────

  "gradientCast" should "return field unchanged at the source" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      val metric = neighborValues[Double, Double](1.0)
      gradientCast[Int, Int, Double](true, 42, metric, identity)
    engine.cycle() shouldBe 42

  it should "return field for an isolated non-source node" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      val metric = neighborValues[Double, Double](1.0)
      gradientCast[Int, Int, Double](false, 42, metric, identity)
    engine.cycle() shouldBe 42

  "broadcast" should "return field at the source (isolated)" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      val metric = neighborValues[Double, Double](1.0)
      broadcast[Int, Int, Double](true, 99, metric)
    engine.cycle() shouldBe 99

  // ── network convergence tests (2×2 Moore grid, all 4 nodes connected) ───

  "broadcast" should "propagate the source value to all nodes" in:
    val env = mooreGrid[Int, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      broadcast[Int, Int, Double](localId == 0, 42, metric)
    (0 until 4).foreach(_ => env.cycleInOrder())
    forAll(env.status.toSeq): (_, result) =>
      result shouldBe 42

  it should "propagate the nearest-source value with multiple sources" in:
    // nodes 0 and 3 are both sources; all 4 nodes are 1 hop from each other.
    // Tie-breaking by smaller DeviceId → source 0 (value 10) wins for nodes 1 and 2.
    val env = mooreGrid[Int, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      val field = if localId == 0 then 10 else if localId == 3 then 20 else 0
      broadcast[Int, Int, Double](localId == 0 || localId == 3, field, metric)
    (0 until 4).foreach(_ => env.cycleInOrder())
    env.status(1) shouldBe 10
    env.status(2) shouldBe 10

  "gradientCast" should "match distanceTo when accumulate adds one hop per step" in:
    val envGC = mooreGrid[Double, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      gradientCast[Double, Double, Double](localId == 0, 0.0, metric, _ + 1.0)
    val envDT = mooreGrid[Double, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      distanceTo[Double, Double](localId == 0, metric)
    (0 until 4).foreach(_ => envGC.cycleInOrder())
    (0 until 4).foreach(_ => envDT.cycleInOrder())
    envGC.status.toSeq.sorted shouldBe envDT.status.toSeq.sorted

  "distanceBetween" should "equal the hop distance between source and target" in:
    // 2×2 all-connected: nodes 0 and 3 are directly connected (1 hop)
    val env = mooreGrid[Double, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      distanceBetween[Double](localId == 0, localId == 3, metric)
    (0 until 5).foreach(_ => env.cycleInOrder())
    forAll(env.status.toSeq): (_, result) =>
      result shouldBe 1.0

  "channel" should "include source and target and exclude off-path nodes" in:
    // dist(0)+dist(3) = 1 for nodes 0 and 3; = 2 for nodes 1 and 2.
    // distBetween(0,3)=1; width=0.5 → threshold=1.5 → only nodes 0 and 3 qualify.
    val env = mooreGrid[Boolean, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val metric = neighborValues[Double, Double](1.0)
      channel[Double](localId == 0, localId == 3, metric, 0.5)
    (0 until 6).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe true
    env.status(3) shouldBe true
    env.status(1) shouldBe false
    env.status(2) shouldBe false

end GradientCastLibraryTests
