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

class JVMApiTest extends AnyWordSpec with should.Matchers with Inspectors:

  "Aggregate programs" should:
    "work on JVM platform with portable libraries without syntactic changes w.r.t. Scala ones" when:
      "a simple aggregate program is run" in:
        type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData

        def aggregateProgram(using Lang) =
          exchange(localId): n =>
            (n, n)
          .neighborValues

        val env = mooreGrid(3, 3, exchangeContextFactory)(aggregateProgram)
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, field) =>
          forAll(field.toMap): (nid, nvalue) =>
            nvalue shouldBe (if nid <= id then nid else id)

      "branching domain operation is run" in:
        type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData &
          BranchingSyntax

        def branchProgram(using Lang) =
          branch(localId % 2 == 0) {
            exchange(true)(n => (n, n))
          } {
            exchange(false)(n => (n, n))
          }

        val env = mooreGrid(3, 3, exchangeContextFactory)(branchProgram)
        env.cycleInOrder()
        env.cycleInReverseOrder()
        forAll(env.status): (id, result) =>
          val alignedNeighbors = env.neighborsOf(env.nodes.find(_.id == id).get).filter(_.id % 2 == id % 2)
          result.neighborValues should contain theSameElementsAs alignedNeighbors.map(_.id).map(_ -> (id % 2 == 0))

end JVMApiTest
