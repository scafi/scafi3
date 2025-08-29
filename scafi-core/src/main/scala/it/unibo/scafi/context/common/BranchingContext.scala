package it.unibo.scafi.context.common

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.calculus.BranchingCalculus
import it.unibo.scafi.utils.AlignmentManager
import it.unibo.scafi.language.common.BranchingLanguage

trait BranchingContext extends BranchingCalculus, BranchingLanguage:
  self: AggregateFoundation & AlignmentManager =>

  override def br[T](condition: Boolean)(th: => T)(el: => T): T =
    alignmentScope(s"branch/$condition"): () =>
      if condition then th else el
