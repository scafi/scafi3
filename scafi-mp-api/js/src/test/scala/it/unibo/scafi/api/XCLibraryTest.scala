package it.unibo.scafi.api

import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.context.xc.ExchangeAggregateContext

import org.scalatest.matchers.should
import org.scalatest.Inspectors
import org.scalatest.wordspec.AnyWordSpec

class XCLibraryTest extends AnyWordSpec with should.Matchers with Inspectors:
  import XCLibraryTest.*

  "Aggregate programs" should:
    "work on JS platform using portable libraries" when:
      "a simple exchange-based aggregate program is run" in:
        def aggregateProgram(lang: XCLibrary): scalajs.js.Map[Int, Int] =
          lang
            .exchange(lang.of(lang.localId)): n =>
              (n, n)
            .neighborValues

        val env = mooreGrid(3, 3, exchangeContextFactory):
          aggregateProgram(XCLibrary())

        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, field) =>
          forAll(field.toMap): (nid, nvalue) =>
            nvalue shouldBe (if nid <= id then nid else id)

      "domain branching operation is run" in:
        def aggregateProgram(lang: XCLibrary) =
          lang.branch(lang.localId.isEven) {
            lang.exchange(lang.of(true))(n => (n, n))
          } {
            lang.exchange(lang.of(false))(n => (n, n))
          }

        val env = mooreGrid(3, 3, exchangeContextFactory):
          aggregateProgram(XCLibrary())
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, result) =>
          val alignedNeighbors = env.neighborsOf(env.nodes.find(_.id == id).get).map(_.id).filter(_.hasSameParityAs(id))
          result.neighborValues.toMap should contain theSameElementsAs alignedNeighbors.map(_ -> id.isEven)
end XCLibraryTest

object XCLibraryTest:
  extension (id: Int)
    def isEven: Boolean = id % 2 == 0
    def hasSameParityAs(other: Int): Boolean = id.isEven == other.isEven
