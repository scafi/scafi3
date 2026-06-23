package it.unibo.scafi.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.libraries.All.{ localId, neighborValues }
import it.unibo.scafi.libraries.CollectionLibrary.{
  collect,
  collectCount,
  collectMean,
  collectValuesByDevices,
  findParent,
  findParentOpt,
}
import it.unibo.scafi.libraries.GradientLibrary.distanceTo
import it.unibo.scafi.message.{ Codable, Codables }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager
import it.unibo.scafi.utils.boundaries.CommonBoundaries.given

import org.scalatest.Inspectors

class CollectionLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type TestCtx = ExchangeAggregateContext[Int]

  // ── findParent / findParentOpt (pure, mock) ──────────────────────────────

  given lang: AggregateFoundationMock = AggregateFoundationMock()
  // lang.DeviceId = Int; mock device field = Seq(0,1,...,9); self at index 0.

  "findParentOpt" should "return None when the device is the local minimum (the sink)" in:
    val potential = lang.mockField(Seq(0.0, 1.0, 2.0)) // self potential 0.0 is the smallest
    findParentOpt(potential) shouldBe None

  it should "return the strictly-lower-potential neighbour" in:
    val potential = lang.mockField(Seq(2.0, 1.0, 3.0)) // neighbour 1 has potential 1.0 < 2.0
    findParentOpt(potential) shouldBe Some(1)

  it should "break ties on potential by the smaller device id" in:
    val potential = lang.mockField(Seq(5.0, 1.0, 1.0)) // neighbours 1 and 2 tie at 1.0
    findParentOpt(potential) shouldBe Some(1)

  "findParent" should "return the DeviceId upper bound for a local minimum" in:
    val potential = lang.mockField(Seq(0.0, 1.0, 2.0))
    findParent(potential) shouldBe Int.MaxValue

  // ── collect network convergence ──────────────────────────────────────────

  "collectCount" should "accumulate the total reachable device count at the sink" in:
    val env = mooreGrid[Long, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectCount[Double](potential, predicate = true)
    (0 until 20).foreach(_ => env.cycleInOrder())
    // 3×3 grid → 9 reachable devices, all collected to the sink (no double counting).
    env.status(0) shouldBe 9L

  "collect" should "sum local contributions to the sink, matching collectCount" in:
    val env = mooreGrid[Long, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collect[Long, Double, Long](potential, 1L, _ + _, 0L)
    (0 until 20).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe 9L

  "collectMean" should "report the true mean of a constant field at the sink" in:
    val env = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectMean[Double](potential, value = 5.0)
    (0 until 20).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe (5.0 +- 1e-9)

  "collectValuesByDevices" should "gather every device's value into a map at the sink" in:
    val env = mooreGrid[Map[Int, Int], TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectValuesByDevices[Double, Int](potential, localId * 10)
    (0 until 20).foreach(_ => env.cycleInOrder())
    val expected = (0 until 9).map(id => id -> id * 10).toMap
    env.status(0) shouldBe expected

  it should "not double-count on a larger grid with many equidistant paths" in:
    // A 4×4 Moore grid has many shortest paths to the corner sink; the single-parent
    // tie-break must still yield exactly one parent per node (total = 16, not more).
    val env = mooreGrid[Long, TestCtx, IntNetworkManager](4, 4, exchangeContextFactory, inMemoryNetwork):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectCount[Double](potential, predicate = true)
    (0 until 30).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe 16L

  // ── isolated node (totality) ─────────────────────────────────────────────

  "collectCount" should "report 1 on an isolated sink" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectCount[Double](potential, predicate = true)
    (0 until 5).map(_ => engine.cycle()).last shouldBe 1L

  "collectMean" should "report the device's own value on an isolated sink (no division by zero)" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      val potential = neighborValues[Double, Double](distanceTo[Double, Double](localId == 0, neighborValues(1.0)))
      collectMean[Double](potential, value = 7.0)
    (0 until 5).map(_ => engine.cycle()).last shouldBe (7.0 +- 1e-9)

end CollectionLibraryTests
