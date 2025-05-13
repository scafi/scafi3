package it.unibo.scafi.language.common.syntax

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.libraries.BranchingLibrary.branch
import it.unibo.scafi.libraries.CommonLibrary.localId
import it.unibo.scafi.libraries.FieldCalculusLibrary.neighborValues
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.test.AggregateProgramProbe
import it.unibo.scafi.test.environment.Grids.mooreGrid

import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

trait BranchingSyntaxTest extends AggregateProgramProbe:
  self: AnyFlatSpecLike & should.Matchers & Inspectors =>

  private type BranchingContext =
    AggregateContext & AggregateFoundation { type DeviceId = Int } & BranchingSyntax & FieldCalculusSyntax

  def branchSpecification[Context <: BranchingContext](
      contextFactory: (Int, NetworkManager { type DeviceId = Int }) => Context,
  ): Unit =
    "Branch operator" should "partition the network based on a condition" in:
      val env = mooreGrid(5, 5, contextFactory):
        branch(localId % 2 == 0)(true)(false)
      // All even nodes should return true, odd nodes should return false
      env.cycleInOrder()
      forAll(env.status) { (id, result) => id % 2 == 0 shouldBe result }

    it should "restrict the field visibility based on the condition" in:
      val env = mooreGrid(5, 5, contextFactory):
        branch(localId % 2 == 0) { neighborValues(localId).toList } { neighborValues(localId).toList }
      // All even nodes have even neighbors, odd nodes have odd neighbors
      (0 until 5).foreach { _ => env.cycleInOrder() }
      forAll(env.status) { (id, neighborIds) => forAll(neighborIds) { elem => elem % 2 shouldBe id % 2 } }
end BranchingSyntaxTest
