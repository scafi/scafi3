package it.unibo.scafi.context.common

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.Branching
import it.unibo.scafi.language.common.semantic.BranchingSemantic
import it.unibo.scafi.utils.AlignmentManager

trait BranchingContext extends Branching, BranchingSemantic:
  self: AggregateFoundation & AlignmentManager =>

  override def br[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T =
    alignmentScope(s"branch/$condition"): () =>
      if condition then trueBranch else falseBranch
