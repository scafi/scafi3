package it.unibo.scafi.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.libraries.StateLibrary.{
  captureChange,
  constant,
  countChanges,
  delay,
  goesDown,
  goesUp,
  keep,
  keepTrue,
  once,
  remember,
  roundCounter,
}
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

class StateLibraryTests extends UnitTest:

  private type Ctx = ExchangeAggregateContext[Int]
  private val network = NoNeighborsNetworkManager(localId = 0)

  private def runRounds[A](rounds: Int)(program: Ctx ?=> A): Seq[A] =
    val engine = ScafiEngine(network, exchangeContextFactory)(program)
    (0 until rounds).map(_ => engine.cycle())

  "roundCounter" should "count rounds starting from 1" in:
    runRounds(5)(roundCounter) shouldBe Seq(1L, 2L, 3L, 4L, 5L)

  "remember" should "return the first-round value on every subsequent round" in:
    var n = 0
    val results = runRounds(4):
      n += 1
      remember(n)
    results shouldBe Seq(1, 1, 1, 1)

  "constant" should "behave identically to remember" in:
    var n = 0
    val results = runRounds(4):
      n += 1
      constant(n)
    results shouldBe Seq(1, 1, 1, 1)

  "keep" should "latch to the first Some and update only when a new Some arrives" in:
    val inputs: Seq[Option[Int]] = Seq(Some(1), None, Some(2), None)
    var idx = 0
    val results = runRounds(inputs.length):
      val v = keep(inputs(idx))
      idx += 1
      v
    results shouldBe Seq(Some(1), Some(1), Some(2), Some(2))

  "keepTrue" should "latch to true forever once the condition first holds" in:
    val inputs = Seq(false, true, false)
    var idx = 0
    val results = runRounds(inputs.length):
      val v = keepTrue(inputs(idx))
      idx += 1
      v
    results shouldBe Seq(false, true, true)

  "captureChange (initially=true)" should "detect value changes with first round flagged" in:
    val xs = Seq(1, 1, 2, 2, 3)
    var idx = 0
    val results = runRounds(xs.length):
      val v = captureChange(xs(idx), true)
      idx += 1
      v
    results shouldBe Seq(true, false, true, false, true)

  "captureChange (initially=false)" should "suppress the first-round flag" in:
    val xs = Seq(1, 1, 2, 2, 3)
    var idx = 0
    val results = runRounds(xs.length):
      val v = captureChange(xs(idx), false)
      idx += 1
      v
    results shouldBe Seq(false, false, true, false, true)

  "captureChange (no initially)" should "default to initially=true" in:
    val xs = Seq(1, 1, 2)
    var idx = 0
    val results = runRounds(xs.length):
      val v = captureChange(xs(idx))
      idx += 1
      v
    results shouldBe Seq(true, false, true)

  "countChanges (initially=true)" should "track cumulative change count and per-round flag" in:
    val xs = Seq(1, 1, 2, 2, 3)
    var idx = 0
    val results = runRounds(xs.length):
      val v = countChanges(xs(idx), true)
      idx += 1
      v
    results shouldBe Seq((1L, true), (1L, false), (2L, true), (2L, false), (3L, true))

  "countChanges (no initially)" should "default to initially=true" in:
    val xs = Seq(1, 2)
    var idx = 0
    val results = runRounds(xs.length):
      val v = countChanges(xs(idx))
      idx += 1
      v
    results shouldBe Seq((1L, true), (2L, true))

  "goesUp" should "fire true on the false→true transition only" in:
    val inputs = Seq(false, true, true, false, true)
    var idx = 0
    val results = runRounds(inputs.length):
      val v = goesUp(inputs(idx))
      idx += 1
      v
    results shouldBe Seq(false, true, false, false, true)

  "goesDown" should "fire true on the true→false transition only" in:
    val inputs = Seq(false, true, true, false, true)
    var idx = 0
    val results = runRounds(inputs.length):
      val v = goesDown(inputs(idx))
      idx += 1
      v
    results shouldBe Seq(false, false, false, true, false)

  "delay" should "echo current value on first round then lag by one round" in:
    val inputs = Seq(10, 20, 30)
    var idx = 0
    val results = runRounds(inputs.length):
      val v = delay(inputs(idx))
      idx += 1
      v
    results shouldBe Seq(10, 10, 20)

  "once" should "return Some on the first round and None thereafter" in:
    var n = 0
    val results = runRounds(4):
      n += 1
      once(n)
    results shouldBe Seq(Some(1), None, None, None)

end StateLibraryTests
