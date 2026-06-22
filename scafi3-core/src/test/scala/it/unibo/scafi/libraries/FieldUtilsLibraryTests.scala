package it.unibo.scafi.libraries

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.libraries.All.{ localId, neighborValues }
import it.unibo.scafi.libraries.FieldUtilsLibrary.{ maxHoodSelector, mergeHood, minHoodSelector }
import it.unibo.scafi.message.{ Codable, Codables }
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.environment.IntNetworkManager

import org.scalatest.Inspectors

class FieldUtilsLibraryTests extends UnitTest, Inspectors:

  given [V]: Codable[V, V] = Codables.forInMemoryCommunications

  // ── unit tests (pure single-device, no network) ──────────────────────────

  given lang: AggregateFoundationMock = AggregateFoundationMock()
  // lang.DeviceId = Int; mock device field = Seq(0,1,2,...,9) with self at index 0

  "minHoodSelector" should "return data of the neighbour with minimum key (self excluded)" in:
    // withoutSelf leaves: (key=1, dev=1, data="b"), (key=2, dev=2, data="c")
    val key = lang.mockField(Seq(3, 1, 2))
    val data = lang.mockField(Seq("a", "b", "c"))
    minHoodSelector(key, data, "z") shouldBe "b"

  "maxHoodSelector" should "return data of the neighbour with maximum key (self excluded)" in:
    // withoutSelf leaves: (key=1, dev=1, data="b"), (key=2, dev=2, data="c") → max key=2 → "c"
    val key = lang.mockField(Seq(3, 1, 2))
    val data = lang.mockField(Seq("a", "b", "c"))
    maxHoodSelector(key, data, "z") shouldBe "c"

  "minHoodSelector" should "return default when no neighbours remain after excluding self" in:
    val key = lang.mockField(Seq(42))
    val data = lang.mockField(Seq("only-self"))
    minHoodSelector(key, data, "default") shouldBe "default"

  "maxHoodSelector" should "return default when no neighbours remain after excluding self" in:
    val key = lang.mockField(Seq(42))
    val data = lang.mockField(Seq("only-self"))
    maxHoodSelector(key, data, "default") shouldBe "default"

  "minHoodSelector" should "break ties by the smaller device id" in:
    // withoutSelf: (key=1, dev=1, data="b"), (key=1, dev=2, data="c") → tie → dev=1 wins
    val key = lang.mockField(Seq(99, 1, 1))
    val data = lang.mockField(Seq("self", "b", "c"))
    minHoodSelector(key, data, "z") shouldBe "b"

  "maxHoodSelector" should "break ties by the larger device id" in:
    // withoutSelf: (key=5, dev=1, data="b"), (key=5, dev=2, data="c") → tie → dev=2 wins
    val key = lang.mockField(Seq(0, 5, 5))
    val data = lang.mockField(Seq("self", "b", "c"))
    maxHoodSelector(key, data, "z") shouldBe "c"

  "mergeHood" should "merge maps summing values on key collision" in:
    val maps = lang.mockField(Seq(Map("a" -> 1), Map("a" -> 2, "b" -> 3)))
    mergeHood(maps)(_ + _) shouldBe Map("a" -> 3, "b" -> 3)

  it should "keep the first value on key collision when overwrite is (x,_)=>x" in:
    val maps = lang.mockField(Seq(Map("a" -> 1), Map("a" -> 2, "b" -> 3)))
    mergeHood(maps)((x, _) => x) shouldBe Map("a" -> 1, "b" -> 3)

  it should "return empty Map for an empty field" in:
    val maps = lang.mockField(Seq.empty[Map[String, Int]])
    mergeHood(maps)(_ + _) shouldBe Map.empty[String, Int]

  // ── network test ─────────────────────────────────────────────────────────

  "minHoodSelector" should "select the minimum-key neighbour over a 2×2 Moore grid" in:
    type TestCtx = ExchangeAggregateContext[Int]
    // Explicit type args so the compiler knows TestCtx.DeviceId = Int
    // and can resolve Ordering[Int] from the standard library.
    val env = mooreGrid[Int, TestCtx, IntNetworkManager](2, 2, exchangeContextFactory, inMemoryNetwork):
      val ids = neighborValues(localId)
      minHoodSelector(ids, ids, Int.MaxValue)

    (0 until 2).foreach(_ => env.cycleInOrder())

    // 2×2 Moore grid (all-connected): nodes 0,1,2,3
    //   node 0 → minimum neighbour id = 1 (excludes self 0)
    //   nodes 1,2,3 → minimum neighbour id = 0
    val expected = Map(0 -> 1, 1 -> 0, 2 -> 0, 3 -> 0)
    forAll(expected.toSeq): (id, minNbr) =>
      env.status.get(id) shouldBe Some(minNbr)
end FieldUtilsLibraryTests
