package it.unibo.scafi.context.xc

import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.common.syntax.BranchingSyntaxTest
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntaxTest
import it.unibo.scafi.libraries.All.{ exchange, localId, returning }
import it.unibo.scafi.message.ValueTree.NoPathFoundException
import it.unibo.scafi.test.AggregateProgramProbe
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

import cats.syntax.all.toFunctorOps
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ExchangeAggregateContextTest
    extends AnyFlatSpecLike,
      should.Matchers,
      Inspectors,
      AggregateProgramProbe,
      BranchingSyntaxTest,
      FieldCalculusSyntaxTest:
  type Lang = ExchangeAggregateContext[Int]

  "ExchangeContext" should behave like branchSpecification(exchangeContextFactory)
  it should behave like fieldCalculusSpecification(exchangeContextFactory)

  "Exchange construct" should "return a different value than the one sent" in:
    def programRetSend(using Lang) = exchange(localId) { x => returning(x.map(_ + 1)) send x }.default
    val (result, exportValue) =
      roundForAggregateProgram(0, NoNeighborsNetworkManager[Int](), exchangeContextFactory)(programRetSend)
    result shouldBe 1
    val singlePath = exportValue(0).paths.head // There is only one path
    try exportValue(0).apply[Int](singlePath) shouldBe 0
    catch case _: NoPathFoundException => fail("The path should exist, but it was not found.")
end ExchangeAggregateContextTest
