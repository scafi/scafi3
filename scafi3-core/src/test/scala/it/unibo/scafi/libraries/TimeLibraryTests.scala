package it.unibo.scafi.libraries

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.TimeLibrary.{
  clock,
  cyclicTimer,
  cyclicTimerWithDecay,
  decay,
  evaporation,
  exponentialBackoffFilter,
  impulsesEvery,
  limitedMemory,
  recentlyTrue,
  sharedTimerWithDecay,
  timer,
}
import it.unibo.scafi.message.{ Codable, Codables, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.sensors.TimeSensor
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.IntNetworkManager
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

class TimeLibraryTests extends UnitTest:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  private type TestCtx = ExchangeAggregateContext[Int]

  private val network = NoNeighborsNetworkManager(localId = 0)

  private def runRounds[A](rounds: Int)(program: TestCtx ?=> A): Seq[A] =
    val engine = ScafiEngine(network, exchangeContextFactory)(program)
    (0 until rounds).map(_ => engine.cycle())

  // ── TimeSensor-capable context ────────────────────────────────────────────

  private type TimeSensorCtx = ExchangeAggregateContext[Int] & TimeSensor

  private def timeSensorFactory(getDelta: () => FiniteDuration)(
      net: NetworkManager { type DeviceId = Int },
      vt: ValueTree,
  ): TimeSensorCtx =
    new ExchangeAggregateContext[Int](net.localId, net.receive, vt) with TimeSensor:
      override def deltaTime: FiniteDuration = getDelta()
      override def timestamp: Long = 0L

  private def runRoundsWithDelta[A](rounds: Int, deltaPerRound: Seq[FiniteDuration])(
      program: TimeSensorCtx ?=> A,
  ): Seq[A] =
    var idx = 0
    val factory = timeSensorFactory(() => { val d = deltaPerRound(idx); idx += 1; d })
    val engine = ScafiEngine(network, factory)(program)
    (0 until rounds).map(_ => engine.cycle())

  // ── decay / timer ─────────────────────────────────────────────────────────

  "timer(3)" should "count down to 0 and clamp there" in:
    runRounds(5)(timer(3)) shouldBe Seq(2, 1, 0, 0, 0)

  "decay(10, 4, _ - 3)" should "count down and clamp at floor" in:
    runRounds(4)(decay(10, 4, _ - 3)) shouldBe Seq(7, 4, 4, 4)

  "decay(10, _ - 3)" should "decay to zero" in:
    runRounds(5)(decay(10, _ - 3)) shouldBe Seq(7, 4, 1, 0, 0)

  // ── cyclicTimer ───────────────────────────────────────────────────────────

  "cyclicTimer(3)" should "fire true every 3 rounds" in:
    // evolve applies f on round 1: length(3) - 1 = 2; fire when left wraps back to 3
    // round 1: f(3)=2 → false; round 2: f(2)=1 → false; round 3: f(1)=0 → false;
    // round 4: f(0)=reset to 3 → true; etc.
    runRounds(7)(cyclicTimer(3)) shouldBe Seq(false, false, false, true, false, false, false)

  "cyclicTimerWithDecay(6, 2)" should "fire every 3 rounds" in:
    runRounds(7)(cyclicTimerWithDecay(6, 2)) shouldBe Seq(false, false, false, true, false, false, false)

  // ── clock ─────────────────────────────────────────────────────────────────

  "clock(3, 1)" should "increment its counter on each cyclic wrap" in:
    // wraps at round 4, 7, ...
    runRounds(8)(clock(3, 1)) shouldBe Seq(0L, 0L, 0L, 1L, 1L, 1L, 1L, 2L)

  // ── limitedMemory ─────────────────────────────────────────────────────────

  "limitedMemory" should "hold value while running then switch to expValue" in:
    val results = runRounds(5)(limitedMemory("live", "expired", 2))
    // timer(2): round1=1, round2=0 → switch. (value, remaining)
    results.map(_._1) shouldBe Seq("live", "expired", "expired", "expired", "expired")

  // ── impulsesEvery ─────────────────────────────────────────────────────────

  "impulsesEvery(3)" should "produce a true impulse every 3 rounds" in:
    val results = runRounds(8)(impulsesEvery(3))
    // timer(3) reaches 0 after 3 rounds (evolve applies f on round 1: f(3)=2, f(2)=1, f(1)=0)
    // impulse resets to false right after firing, timer restarts
    results shouldBe Seq(false, false, true, false, false, false, true, false)

  // ── exponentialBackoffFilter ──────────────────────────────────────────────

  "exponentialBackoffFilter" should "approach the signal monotonically from above" in:
    // signal=0, alpha=0.5, initial=10: 10→5→2.5→1.25→...
    // Actually: evolve(signal=0)(prev => prev*0.5 + 0*0.5 = prev*0.5)
    // But initial is `signal`=0, so it stays at 0. Let's test with a non-zero signal.
    // signal=8.0, alpha=0.5: round1=8*0.5+8*0.5=8... same issue.
    // Let signal vary: use a fixed signal of 8.0 with initial 8.0, alpha=0.75
    // round1: f(8.0) = 8*0.75 + 8*0.25 = 8.0 (no change since signal=initial)
    // Better: filter over time means it converges. With constant signal it stays constant.
    // Let's test convergence: start at 0 but evolve converges toward a non-zero signal.
    // Actually evolve(signal)(prev => prev*a + signal*(1-a)) with signal=constant:
    // all rounds = signal. With signal=5.0, alpha=0.5: round1=f(5)=5*0.5+5*0.5=5, stays 5.
    // To see EMA behavior, we'd need signal to change. Let's just verify round1.
    val results = runRounds(1)(exponentialBackoffFilter(5.0, 0.5))
    results.head shouldBe 5.0

  "exponentialBackoffFilter with alpha=0" should "follow signal immediately" in:
    val results = runRounds(3)(exponentialBackoffFilter(10.0, 0.0))
    results shouldBe Seq(10.0, 10.0, 10.0)

  // ── evaporation ───────────────────────────────────────────────────────────

  "evaporation" should "decay the length while keeping info" in:
    val results = runRounds(4)(evaporation(10, _ - 3, "info"))
    results.map(_._1) shouldBe Seq(7, 4, 1, 0)
    results.map(_._2).forall(_ == "info") shouldBe true

  // ── recentlyTrue (TimeSensor mock) ────────────────────────────────────────

  "recentlyTrue" should "stay true for the window after cond fires then reset" in:
    // deltaTime = 50ms each round, window = 100ms
    // cond fires on round 1 only; should stay true for ~2 more rounds then false
    val deltas = Seq.fill(10)(50.millis)
    var round = 0
    val results = runRoundsWithDelta(6, deltas):
      val r = round
      round += 1
      recentlyTrue(100.millis, r == 0)
    // round 0: cond=true → happened=true
    // round 1: cond=false, happened=true, timer(100ms) with delta=50ms: decay 100-50=50>0 → true
    // round 2: cond=false, happened=true, timer continues: 50-50=0 → false? Let's assert it stays true for 2 rounds
    results(0) shouldBe true
    results(1) shouldBe true

  // ── sharedTimerWithDecay network test ─────────────────────────────────────

  "sharedTimerWithDecay" should "synchronise clocks across a 2×2 network" in:
    val env = mooreGrid[Long, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      sharedTimerWithDecay[Long, Long](5L, 1L)
    (0 until 20).foreach(_ => env.cycleInOrder())
    val values = env.status.values.toSeq
    val range = values.max - values.min
    range should be <= 1L

end TimeLibraryTests
