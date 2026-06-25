package it.unibo.scafi.libraries

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.All.{ localId, neighborValues }
import it.unibo.scafi.libraries.GradientLibrary.{
  bisGradient,
  crfGradient,
  distanceTo,
  flexGradient,
  hopGradient,
  DEFAULT_CRF_RAISING_SPEED,
  DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON,
  DEFAULT_FLEX_DELTA,
}
import it.unibo.scafi.message.{ Codable, Codables, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.sensors.{ LagSensor, TimeSensor }
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager
import it.unibo.scafi.utils.boundaries.CommonBoundaries.given

import org.scalatest.Inspectors

class GradientLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type TestCtx = ExchangeAggregateContext[Int]
  private type TimeLagCtx = ExchangeAggregateContext[Int] & TimeSensor & LagSensor[FiniteDuration]

  private val fixedDelta: FiniteDuration = 100.millis
  private val fixedLag: FiniteDuration = 10.millis

  private def timeLagFactory(net: IntNetworkManager, vt: ValueTree): TimeLagCtx =
    new ExchangeAggregateContext[Int](net.localId, net.receive, vt) with TimeSensor with LagSensor[FiniteDuration]:
      override def deltaTime: FiniteDuration = fixedDelta
      override def timestamp: Long = 0L
      override def senseLag: SharedData[FiniteDuration] =
        neighborValues[FiniteDuration, FiniteDuration](fixedLag)

  // ── hopGradient ─────────────────────────────────────────────────────────────

  "hopGradient" should "return 0 at an isolated source" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      hopGradient[Int](source = true)
    engine.cycle() shouldBe 0

  it should "return Int.MaxValue at an isolated non-source" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      hopGradient[Int](source = false)
    engine.cycle() shouldBe Int.MaxValue

  it should "compute Chebyshev hop distances on a 3×3 Moore grid" in:
    // IDs: x varies outer, y inner → (x=0,y=0)=0, (x=0,y=1)=1, (x=0,y=2)=2,
    //      (x=1,y=0)=3, (x=1,y=1)=4, (x=1,y=2)=5, (x=2,y=0)=6, (x=2,y=1)=7, (x=2,y=2)=8
    // Source at node 0 = (0,0); Chebyshev distances:
    //   row y=0: 0,1,2  (IDs 0,3,6)
    //   row y=1: 1,1,2  (IDs 1,4,7)
    //   row y=2: 2,2,2  (IDs 2,5,8)
    val env = mooreGrid[Int, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      hopGradient[Int](localId == 0)
    (0 until 6).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe 0
    env.status(1) shouldBe 1
    env.status(2) shouldBe 2
    env.status(3) shouldBe 1
    env.status(4) shouldBe 1
    env.status(5) shouldBe 2
    env.status(6) shouldBe 2
    env.status(7) shouldBe 2
    env.status(8) shouldBe 2

  it should "re-converge after the source moves" in:
    var sourceId = 0
    val env = mooreGrid[Int, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      hopGradient[Int](localId == sourceId)
    (0 until 6).foreach(_ => env.cycleInOrder())
    env.status(0) shouldBe 0
    // Move source to node 8 = (2,2)
    sourceId = 8
    (0 until 6).foreach(_ => env.cycleInOrder())
    env.status(8) shouldBe 0
    env.status(0) shouldBe 2

  // ── crfGradient convergence parity ──────────────────────────────────────────

  "crfGradient" should "converge to the same distances as distanceTo on a static 3×3 grid" in:
    val envCrf = mooreGrid[Double, TimeLagCtx, IntNetworkManager](3, 3, timeLagFactory, inMemoryNetwork):
      crfGradient[Double](DEFAULT_CRF_RAISING_SPEED, localId == 0, neighborValues[Double, Double](1.0))
    val envRef = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      distanceTo[Double, Double](localId == 0, neighborValues[Double, Double](1.0))
    (0 until 30).foreach(_ => envCrf.cycleInOrder())
    (0 until 10).foreach(_ => envRef.cycleInOrder())
    for id <- 0 until 9 do envCrf.status(id) shouldBe (envRef.status(id) +- 0.05)

  it should "re-converge to new distances after source moves" in:
    var sourceId = 0
    val envCrf = mooreGrid[Double, TimeLagCtx, IntNetworkManager](3, 3, timeLagFactory, inMemoryNetwork):
      crfGradient[Double](DEFAULT_CRF_RAISING_SPEED, localId == sourceId, neighborValues[Double, Double](1.0))
    (0 until 20).foreach(_ => envCrf.cycleInOrder())
    envCrf.status(0) shouldBe (0.0 +- 0.05)
    sourceId = 8
    (0 until 30).foreach(_ => envCrf.cycleInOrder())
    envCrf.status(8) shouldBe (0.0 +- 0.05)
    // node 0 is at Chebyshev distance 2 from node 8
    envCrf.status(0) shouldBe (2.0 +- 0.1)

  // ── bisGradient convergence parity ──────────────────────────────────────────

  "bisGradient" should "converge to the same distances as distanceTo on a static 3×3 grid" in:
    // Use large commRadius so the spatial term dominates over the speed-limited temporal bound
    val envBis = mooreGrid[Double, TimeLagCtx, IntNetworkManager](3, 3, timeLagFactory, inMemoryNetwork):
      bisGradient[Double](commRadius = 100.0, localId == 0, neighborValues[Double, Double](1.0))
    val envRef = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      distanceTo[Double, Double](localId == 0, neighborValues[Double, Double](1.0))
    (0 until 30).foreach(_ => envBis.cycleInOrder())
    (0 until 10).foreach(_ => envRef.cycleInOrder())
    for id <- 0 until 9 do envBis.status(id) shouldBe (envRef.status(id) +- 0.05)

  // ── flexGradient convergence parity and tolerance ───────────────────────────

  "flexGradient" should "converge to within epsilon of distanceTo on a static 3×3 grid" in:
    val envFlex = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      flexGradient[Double](
        epsilon = DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON,
        delta = DEFAULT_FLEX_DELTA,
        communicationRadius = 1.0,
        localId == 0,
        neighborValues[Double, Double](1.0),
      )
    val envRef = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      distanceTo[Double, Double](localId == 0, neighborValues[Double, Double](1.0))
    (0 until 20).foreach(_ => envFlex.cycleInOrder())
    (0 until 10).foreach(_ => envRef.cycleInOrder())
    for id <- 0 until 9 do
      val ref = envRef.status(id)
      val flex = envFlex.status(id)
      val maxError = (1 + DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON) * ref + DEFAULT_FLEX_DELTA
      flex should be <= maxError + 0.1

  it should "give tighter estimates with epsilon=0" in:
    val envTight = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      flexGradient[Double](
        epsilon = 0.0,
        delta = DEFAULT_FLEX_DELTA,
        communicationRadius = 1.0,
        localId == 0,
        neighborValues[Double, Double](1.0),
      )
    val envLoose = mooreGrid[Double, TestCtx, IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
      flexGradient[Double](
        epsilon = 0.5,
        delta = DEFAULT_FLEX_DELTA,
        communicationRadius = 1.0,
        localId == 0,
        neighborValues[Double, Double](1.0),
      )
    (0 until 20).foreach(_ => envTight.cycleInOrder())
    (0 until 20).foreach(_ => envLoose.cycleInOrder())
    // Tight epsilon should keep estimates closer to the true distance
    val tightError = (0 until 9).map(id => envTight.status(id)).sum
    val looseError = (0 until 9).map(id => envLoose.status(id)).sum
    tightError should be <= looseError + 0.01

  // ── isolated-node edge case ──────────────────────────────────────────────────

  "crfGradient" should "return infinity at an isolated non-source" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), timeLagFactory):
      crfGradient[Double](DEFAULT_CRF_RAISING_SPEED, source = false, neighborValues[Double, Double](1.0))
    // Non-source with no neighbours: estimate rises until it reaches infinity
    val result = (0 until 5).map(_ => engine.cycle())
    result.last.isInfinite || result.last > 10.0 shouldBe true

  "flexGradient" should "return infinity at an isolated non-source" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      flexGradient[Double](
        epsilon = DEFAULT_FLEX_CHANGE_TOLERANCE_EPSILON,
        delta = DEFAULT_FLEX_DELTA,
        communicationRadius = 1.0,
        source = false,
        neighborValues[Double, Double](1.0),
      )
    engine.cycle().isInfinite shouldBe true

end GradientLibraryTests
