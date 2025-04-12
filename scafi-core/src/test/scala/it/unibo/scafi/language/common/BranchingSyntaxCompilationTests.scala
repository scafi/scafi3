package it.unibo.scafi.language.common

import it.unibo.scafi.context.common.BranchingContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.utils.AlignmentManager
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class BranchingSyntaxCompilationTests extends AnyFlatSpecLike, should.Matchers:

  val branchingContext: BranchingSyntax & AggregateFoundation = new AggregateFoundationMock
    with BranchingContext
    with AlignmentManager {}

  "Branching Syntax" should "compile" in:
    "val _: Int = branchingContext.branch(false)(1)(2)" should compile
    "val _: Int = branchingContext.branch(1)(1)(2)" shouldNot typeCheck
    "val _: String = branchingContext.branch(true)(1)(2)" shouldNot typeCheck
