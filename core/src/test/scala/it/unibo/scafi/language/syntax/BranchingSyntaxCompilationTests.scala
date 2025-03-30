package it.unibo.scafi.language.syntax

import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation

class BranchingSyntaxCompilationTests extends UnitTest:

  val language: BranchingSyntax & AggregateFoundation = new AggregateFoundationMock with BranchingSyntax:
    override def branch[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T = mock[T]

  "Branching Syntax" should "compile" in:
    "val _: Int = language.branch(false)(1)(2)" should compile
    "val _: Int = language.branch(1)(1)(2)" shouldNot typeCheck
    "val _: String = language.branch(true)(1)(2)" shouldNot typeCheck
