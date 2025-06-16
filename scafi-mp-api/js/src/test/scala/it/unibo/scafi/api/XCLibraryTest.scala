package it.unibo.scafi.api

import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.api.ReturnSending.returnSending

import org.scalatest.matchers.should
import org.scalatest.Inspectors
import org.scalatest.wordspec.AnyWordSpec

class XCLibraryTest extends AnyWordSpec with should.Matchers with Inspectors:
  import XCLibraryTest.*

  "Aggregate programs" should:
    "work on JS platform using portable libraries" when:
      "a simple exchange-based aggregate program is run" in:
        def aggregateProgram(library: XCLibrary) =
          library
            .exchange(library.of(library.localId)): n =>
              returnSending(n)
            .neighborValues
            .map(_.asInts)

        val env = mooreGrid(3, 3, exchangeContextFactory)(aggregateProgram(XCLibrary()))
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, field) =>
          forAll(field.toMap): (nid, nvalue) =>
            nvalue shouldBe (if nid <= id then nid else id)

      "domain branching operation is run" in:
        def aggregateProgram(lang: XCLibrary) =
          lang.branch(lang.localId.isEven) {
            lang.exchange(lang.of(true))(n => returnSending(n))
          } {
            lang.exchange(lang.of(false))(n => returnSending(n))
          }

        val env = mooreGrid(3, 3, exchangeContextFactory)(aggregateProgram(XCLibrary()))
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, result) =>
          val alignedNeighbors = env.neighborsOf(env.nodes.find(_.id == id).get).map(_.id).filter(_.hasSameParityAs(id))
          result.neighborValues.toMap should contain theSameElementsAs alignedNeighbors.map(_ -> id.isEven)
end XCLibraryTest

object XCLibraryTest:
  extension (id: Any)
    def isEven: Boolean = id.asInt % 2 == 0
    def hasSameParityAs(other: Any): Boolean = id.asInt.isEven == other.asInt.isEven

  extension (x: (Any, Any)) def asInts: (Int, Int) = (x._1.asInt, x._2.asInt)

  extension (x: Any)
    def asInt: Int = x match
      case i: Int => i
      case _ => throw new IllegalArgumentException(s"Cannot convert $x to Int")
end XCLibraryTest
