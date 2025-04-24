package it.unibo.scafi.context.xc

import it.unibo.scafi.message.Import
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should
import it.unibo.scafi.libraries.All.{ *, given }
import cats.syntax.all.*
import it.unibo.scafi.utils.AggregateProgramProbe
import it.unibo.scafi.utils.network.NoNeighborsNetworkManager

class ExchangeAggregateContextTest extends AnyFlatSpecLike, should.Matchers, AggregateProgramProbe:
  type Lang = ExchangeAggregateContext[Int]
  private class ExchangeContextTest(id: Int, inbound: Import[Int]) extends ExchangeAggregateContext[Int](id, inbound)

  def factory(id: Int, net: NoNeighborsNetworkManager[Int]): Lang = ExchangeContextTest(id, Import.empty)

  "Exchange construct" should "return a different value than the one sent" in:
    def programRetSend(using Lang) = exchange(localId) { x => returning(x.map(_ + 1)) send x }.default
    val (result, exportValue) = roundForAggregateProgram(0, factory)(programRetSend)
    result should be(1)
