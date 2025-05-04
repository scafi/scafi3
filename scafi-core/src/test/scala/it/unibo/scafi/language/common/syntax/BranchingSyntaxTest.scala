package it.unibo.scafi.language.common.syntax

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.libraries.BranchingLibrary.branch
import it.unibo.scafi.libraries.CommonLibrary.{ device, localId }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.utils.AggregateProgramProbe
import it.unibo.scafi.utils.network.NoNeighborsNetworkManager

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

trait BranchingSyntaxTest extends AggregateProgramProbe:
  self: AnyFlatSpecLike & should.Matchers =>

  private type Language = AggregateFoundation { type DeviceId = Int } & BranchingSyntax
  private type BranchingContext =
    AggregateContext { type DeviceId = Int } & AggregateFoundation { type DeviceId = Int } & BranchingSyntax

  private def noNeighborsNetwork: NoNeighborsNetworkManager[Int] = NoNeighborsNetworkManager[Int]()

  def branchSpecification[Context <: BranchingContext](
      contextFactory: (Int, NetworkManager { type DeviceId = Int }) => Context,
  ): Unit =
    "Branch operator" should "partition the network based on a condition" in:
      var alignedOnTrue = Set.empty[Int]
      var alignedOnFalse = Set.empty[Int]
      def branchedProgram(using Language): Set[Int] =
        branch(localId % 2 == 0) {
          val evenDevices = device.toSet; alignedOnTrue = evenDevices
          evenDevices
        } {
          val oddDevices = device.toSet; alignedOnFalse = oddDevices
          oddDevices
        }
      val result = ScafiEngine(0, noNeighborsNetwork, contextFactory)(branchedProgram).cycle()
      alignedOnTrue shouldBe result
      alignedOnTrue should not contain theSameElementsAs(alignedOnFalse)
end BranchingSyntaxTest
