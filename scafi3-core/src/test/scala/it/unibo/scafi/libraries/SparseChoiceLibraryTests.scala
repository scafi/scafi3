package it.unibo.scafi.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.All.neighborValues
import it.unibo.scafi.libraries.SparseChoiceLibrary.{ breakUsingUids, minId, randomUid, sparseChoice }
import it.unibo.scafi.message.{ Codable, Codables, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.sensors.RandomGenerator
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager
import it.unibo.scafi.utils.boundaries.CommonBoundaries.given

import org.scalatest.Inspectors

class SparseChoiceLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type RandomCtx = ExchangeAggregateContext[Int] & RandomGenerator

  // Per-device RNG reseeded with the device id each round: stable per device (so randomUid latches the same value
  // every round) and reproducible across runs — exactly what deterministic leader-election tests need.
  private def randomFactory(net: IntNetworkManager, vt: ValueTree): RandomCtx =
    new ExchangeAggregateContext[Int](net.localId, net.receive, vt) with RandomGenerator:
      private val rng = scala.util.Random(net.localId.toLong)
      override def nextRandom: Double = rng.nextDouble()

  // ── randomUid ────────────────────────────────────────────────────────────

  "randomUid" should "latch the random seed so the uid is stable across rounds" in:
    // A persistent, advancing rng: nextRandom changes every round, yet the latched uid must not.
    val rng = scala.util.Random(7)
    def advancingFactory(net: IntNetworkManager, vt: ValueTree): RandomCtx =
      new ExchangeAggregateContext[Int](net.localId, net.receive, vt) with RandomGenerator:
        override def nextRandom: Double = rng.nextDouble()
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), advancingFactory):
      (randomUid, RandomGenerator.nextRandom)
    val (uid1, draw1) = engine.cycle()
    val (uid2, draw2) = engine.cycle()
    uid1 shouldBe uid2 // latched: the uid is the round-1 seed forever
    uid1._2 shouldBe 0 // the id component is the device id
    draw1 should not equal draw2 // sanity: the underlying stream really is advancing

  // ── minId ────────────────────────────────────────────────────────────────

  "minId" should "return the device's own id on an isolated node" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      minId[Int]
    (0 until 5).map(_ => engine.cycle()).last shouldBe 0

  it should "gossip the global minimum id over a connected grid" in:
    val env =
      mooreGrid[Int, ExchangeAggregateContext[Int], IntNetworkManager](3, 3, exchangeContextFactory, inMemoryNetwork):
        minId[Int]
    (0 until 10).foreach(_ => env.cycleInOrder())
    forAll(env.status.toSeq): (_, minimumId) =>
      minimumId shouldBe 0

  // ── sparseChoice ───────────────────────────────────────────────────────────

  "sparseChoice" should "elect at least one leader on a connected grid" in:
    val env = mooreGrid[Boolean, RandomCtx, IntNetworkManager](5, 5, randomFactory, inMemoryNetwork):
      sparseChoice[(Double, Int), Double](grain = 2.0, neighborValues[Double, Double](1.0))
    (0 until 30).foreach(_ => env.cycleInOrder())
    val leaders = (0 until 25).filter(env.status)
    leaders should not be empty

  it should "be deterministic: the same seed yields the same leader set across runs" in:
    def run(): Set[Int] =
      val env = mooreGrid[Boolean, RandomCtx, IntNetworkManager](5, 5, randomFactory, inMemoryNetwork):
        sparseChoice[(Double, Int), Double](grain = 2.0, neighborValues[Double, Double](1.0))
      (0 until 30).foreach(_ => env.cycleInOrder())
      (0 until 25).filter(env.status).toSet
    run() shouldBe run()

  it should "elect fewer leaders for a larger grain (sparser set)" in:
    def leaderCount(grain: Double): Int =
      val env = mooreGrid[Boolean, RandomCtx, IntNetworkManager](6, 6, randomFactory, inMemoryNetwork):
        sparseChoice[(Double, Int), Double](grain, neighborValues[Double, Double](1.0))
      (0 until 40).foreach(_ => env.cycleInOrder())
      (0 until 36).count(env.status)
    leaderCount(4.0) should be <= leaderCount(1.0)

  it should "settle: the leader set is unchanged once the competition has converged" in:
    val env = mooreGrid[Boolean, RandomCtx, IntNetworkManager](5, 5, randomFactory, inMemoryNetwork):
      sparseChoice[(Double, Int), Double](grain = 2.0, neighborValues[Double, Double](1.0))
    (0 until 40).foreach(_ => env.cycleInOrder())
    val settled = (0 until 25).map(env.status)
    (0 until 5).foreach(_ => env.cycleInOrder())
    (0 until 25).map(env.status) shouldBe settled

  "breakUsingUids" should "elect exactly the single global-minimum-uid device as the sole leader for a large grain" in:
    // With a grain covering the whole 3×3 grid, every device falls within one region, so the lowest UID wins alone.
    val env = mooreGrid[(Boolean, (Double, Int)), RandomCtx, IntNetworkManager](
      3,
      3,
      randomFactory,
      inMemoryNetwork,
    ):
      val uid = randomUid
      (breakUsingUids[(Double, Int), Double](uid, grain = 10.0, neighborValues[Double, Double](1.0)), uid)
    (0 until 30).foreach(_ => env.cycleInOrder())
    val results = (0 until 9).map(env.status)
    val leaders = results.collect { case (true, uid) => uid }
    val globalMinUid = results.map(_._2).min
    leaders shouldBe Seq(globalMinUid)

  "sparseChoice" should "elect the isolated node itself as a leader" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), randomFactory):
      sparseChoice[(Double, Int), Double](grain = 2.0, neighborValues[Double, Double](1.0))
    (0 until 5).map(_ => engine.cycle()).last shouldBe true
end SparseChoiceLibraryTests
