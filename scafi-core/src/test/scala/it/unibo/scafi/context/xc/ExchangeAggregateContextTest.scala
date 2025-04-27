package it.unibo.scafi.context.xc

import it.unibo.scafi.context.common.BranchingContextTest
import it.unibo.scafi.message.{ Import, ValueTree }
import it.unibo.scafi.libraries.All.*
import it.unibo.scafi.message.ValueTree.NoPathFoundException
import it.unibo.scafi.utils.AggregateProgramProbe
import it.unibo.scafi.utils.network.NoNeighborsNetworkManager

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ExchangeAggregateContextTest
    extends AnyFlatSpecLike,
      should.Matchers,
      AggregateProgramProbe,
      BranchingContextTest:
  type Lang = ExchangeAggregateContext[Int]
  private class ExchangeContextTest(id: Int, inbound: Import[Int]) extends ExchangeAggregateContext[Int](id, inbound)

  "ExchangeContext" should behave like branchingSemantic(using ExchangeContextTest(0, Import.empty))

  "Exchange construct" should "return a different value than the one sent" in:
    def programRetSend(using Lang) = exchange(localId) { x => returning(x.map(_ + 1)) send x }.default
    val (result, exportValue) =
      roundForAggregateProgram(0, (id, _) => ExchangeContextTest(id, Import.empty))(programRetSend)
    result shouldBe 1
    val singlePath = exportValue(0).paths.head // There is only one path
    try exportValue(0).apply[Int](singlePath) shouldBe 0
    catch case _: NoPathFoundException => fail("The path should exist, but it was not found.")
end ExchangeAggregateContextTest
