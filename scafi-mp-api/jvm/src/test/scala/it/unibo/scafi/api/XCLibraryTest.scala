package it.unibo.scafi.api

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.api.XCLibrary.{ *, given }
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.common.syntax.BranchingSyntax

import org.scalatest.matchers.should
import org.scalatest.Inspectors
import org.scalatest.wordspec.AnyWordSpec

class XCLibraryTest extends AnyWordSpec with should.Matchers with Inspectors:
  import XCLibraryTest.*

  "Aggregate programs" should:
    "work on JVM platform with portable libraries without syntactic changes w.r.t. Scala ones" when:
      "a simple exchange-based aggregate program is run" in:
        type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData

        def xcAggregateProgram(using Lang) =
          exchange(localId): n =>
            (n, n)

        val env = mooreGrid(3, 3, exchangeContextFactory)(xcAggregateProgram)
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, field) =>
          forAll(field.neighborValues): (nid, nvalue) =>
            nvalue shouldBe (if nid <= id then nid else id)

      "domain branching operation is run" in:
        type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData &
          BranchingSyntax

        def branchAggregateProgram(using Lang) =
          branch(localId.isEven) {
            exchange(true)(n => (n, n))
          } {
            exchange(false)(n => (n, n))
          }

        val env = mooreGrid(3, 3, exchangeContextFactory)(branchAggregateProgram)
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, result) =>
          val alignedNeighbors = env.neighborsOf(env.nodes.find(_.id == id).get).map(_.id).filter(_.hasSameParityAs(id))
          result.neighborValues should contain theSameElementsAs alignedNeighbors.map(_ -> id.isEven)
end XCLibraryTest

object XCLibraryTest:
  extension (id: Int)
    def isEven: Boolean = id % 2 == 0
    def hasSameParityAs(other: Int): Boolean = id.isEven == other.isEven
