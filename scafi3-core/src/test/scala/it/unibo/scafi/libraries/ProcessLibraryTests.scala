package it.unibo.scafi.libraries

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.All.localId
import it.unibo.scafi.libraries.ProcessLibrary.{replicated, sspawn, ProcessOutput, ProcessStatus}
import it.unibo.scafi.libraries.ProcessLibrary.ProcessStatus.{ Bubble, External, Output, Terminated }
import it.unibo.scafi.message.{ Codable, Codables, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.sensors.TimeSensor
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

import org.scalatest.Inspectors

class ProcessLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type ProcCtx = ExchangeAggregateContext[Int] & TimeSensor

  private val fixedDelta: FiniteDuration = 100.millis

  private def timerFactory(net: IntNetworkManager, vt: ValueTree): ProcCtx =
    new ExchangeAggregateContext[Int](net.localId, net.receive, vt) with TimeSensor:
      override def deltaTime: FiniteDuration = fixedDelta
      override def timestamp: Long = 0L

  // ── ProcessStatus enum ─────────────────────────────────────────────────────

  "ProcessStatus" should "have four distinct cases" in:
    List(External, Bubble, Output, Terminated).distinct should have size 4

  "ProcessStatus" should "support equality comparison" in:
    Output shouldBe Output
    External should not be Output
    Terminated should not be Bubble

  // ── ProcessOutput ──────────────────────────────────────────────────────────

  "ProcessOutput" should "preserve result and status" in:
    val out = ProcessOutput(99, Output)
    out.result shouldBe 99
    out.status shouldBe Output

  "ProcessOutput" should "be covariant in result type" in:
    // ProcessOutput[Int] is assignable to ProcessOutput[Any] thanks to +R covariance
    val out: ProcessOutput[Any] = ProcessOutput(42, Bubble)
    out.status shouldBe Bubble

  // ── sspawn single-device tests ─────────────────────────────────────────────

  "sspawn" should "return empty map when no keys are generated" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      sspawn[String, Unit, Int](_ => _ => ProcessOutput(0, Output), Set.empty, ())
    (0 until 5).map(_ => engine.cycle()).last shouldBe Map.empty[String, Int]

  it should "include Output keys in the result" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      sspawn[String, Unit, Int](_ => _ => ProcessOutput(42, Output), Set("k"), ())
    engine.cycle() shouldBe Map("k" -> 42)

  it should "exclude External keys from the result" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      sspawn[String, Unit, Int](
        k => _ => if k == "on" then ProcessOutput(1, Output) else ProcessOutput(0, External),
        Set("on", "off"),
        (),
      )
    engine.cycle() shouldBe Map("on" -> 1)

  it should "exit immediately when process returns Terminated on an isolated node" in:
    // Vacuous ∀ on an empty neighbourhood ⇒ mustExit is true from round 0, so
    // Terminated + mustTerminate ∧ mustExit ⇒ External right away.
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      sspawn[String, Unit, Int](_ => _ => ProcessOutput(0, Terminated), Set("k"), ())
    engine.cycle() shouldBe Map.empty[String, Int]

  it should "pass arguments to the process function" in:
    val engine = ScafiEngine(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory):
      sspawn[String, Int, Int](_ => n => ProcessOutput(n * 2, Output), Set("k"), 21)
    engine.cycle() shouldBe Map("k" -> 42)

  // ── sspawn bubble expansion ────────────────────────────────────────────────

  "sspawn" should "propagate a key from the source to all grid devices" in:
    val env = mooreGrid[Map[Int, Int], ExchangeAggregateContext[Int], IntNetworkManager](
      3,
      3,
      exchangeContextFactory,
      inMemoryNetwork,
    ):
      sspawn[Int, Unit, Int](
        k => _ => ProcessOutput(k, Output),
        if localId == 0 then Set(42) else Set.empty,
        (),
      )
    (0 until 10).foreach(_ => env.cycleInOrder())
    forAll(env.status.values.toSeq): result =>
      result should contain key 42

  it should "carry the correct result value after bubble expansion" in:
    val env = mooreGrid[Map[Int, Int], ExchangeAggregateContext[Int], IntNetworkManager](
      3,
      3,
      exchangeContextFactory,
      inMemoryNetwork,
    ):
      sspawn[Int, Unit, Int](
        k => _ => ProcessOutput(k * 10, Output),
        if localId == 0 then Set(7) else Set.empty,
        (),
      )
    (0 until 10).foreach(_ => env.cycleInOrder())
    forAll(env.status.values.toSeq): result =>
      result.get(7) shouldBe Some(70)

  // ── termination propagation ────────────────────────────────────────────────

  it should "collapse the bubble after all devices signal Terminated" in:
    var terminate = false
    val env = mooreGrid[Map[Int, Int], ExchangeAggregateContext[Int], IntNetworkManager](
      3,
      3,
      exchangeContextFactory,
      inMemoryNetwork,
    ):
      sspawn[Int, Unit, Int](
        k => _ => ProcessOutput(k, if terminate then Terminated else Output),
        if localId == 0 then Set(42) else Set.empty,
        (),
      )
    // Phase 1: let the bubble fill the grid
    (0 until 10).foreach(_ => env.cycleInOrder())
    forAll(env.status.values.toSeq): result =>
      result should contain key 42
    // Phase 2: trigger termination and let the signal propagate
    terminate = true
    (0 until 15).foreach(_ => env.cycleInOrder())
    forAll(env.status.values.toSeq): result =>
      result shouldBe Map.empty[Int, Int]

  // ── replicated ─────────────────────────────────────────────────────────────

  "replicated" should "maintain exactly `replicates` active replicas on a grid after convergence" in:
    // period = 0.5 s, dt = 100 ms → cyclicTimerWithDecay fires every 6 rounds.
    // After 3 periods (≈18 rounds) all devices hold replicas [lastPid-2, lastPid-1, lastPid].
    val env = mooreGrid[Map[Long, Int], ProcCtx, IntNetworkManager](3, 3, timerFactory, inMemoryNetwork):
      replicated[Unit, Int](_ => 42, (), period = 0.5, replicates = 3)
    (0 until 30).foreach(_ => env.cycleInOrder())
    forAll(env.status.values.toSeq): result =>
      result should have size 3
      result.values.foreach(_ shouldBe 42)

  it should "rotate replicas forward as the shared timer advances" in:
    // period=0.5s, dt=100ms: due to Double FP, the cyclic timer fires approximately every 7 rounds.
    // Run 25 rounds (≥3 timer ticks → lastPid≥3) to let the window stabilise, then run 8 more to
    // guarantee at least one additional tick; check the global max pid across all devices advances.
    val env = mooreGrid[Map[Long, Int], ProcCtx, IntNetworkManager](3, 3, timerFactory, inMemoryNetwork):
      replicated[Unit, Int](_ => 0, (), period = 0.5, replicates = 2)
    (0 until 25).foreach(_ => env.cycleInOrder())
    val maxPid1 = env.status.values.toSeq.flatMap(_.keySet).max
    (0 until 8).foreach(_ => env.cycleInOrder())
    val maxPid2 = env.status.values.toSeq.flatMap(_.keySet).max
    maxPid2 should be > maxPid1

end ProcessLibraryTests
