package it.unibo.scafi.context.common

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.utils.matchers.AlignmentMatchers
import org.scalatest.flatspec.AnyFlatSpecLike
import it.unibo.scafi.libraries.All.*
import org.scalatest.matchers.must.Matchers.{ not, theSameElementsAs }
import org.scalatest.matchers.should.Matchers.should

trait BranchingContextTest extends AlignmentMatchers:
  self: AnyFlatSpecLike =>

  type Language = AggregateFoundation { type DeviceId = Int } & AggregateContext & BranchingContext

  def branchingSemantic(using ctx: Language): Unit =
    it should "implement the network partition based on a condition" in:
      var alignedOnTrue = Set.empty[Int]
      var alignedOnFalse = Set.empty[Int]
      def myProgram(using Language): Unit =
        branch(localId % 2 == 0) { alignedOnTrue = device.toSet } { alignedOnFalse = device.toSet }
      myProgram
      alignedOnTrue should not contain theSameElementsAs(alignedOnFalse)
