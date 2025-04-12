package it.unibo.scafi.language.common.semantic

import it.unibo.scafi.language.common.Branching
import it.unibo.scafi.language.common.syntax.BranchingSyntax

/**
 * This trait witnesses the fact that the exchange calculus semantics can be used to implement the branching syntax.
 */
trait BranchingSemantic extends BranchingSyntax:
  self: Branching =>

  override def branch[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T =
    br(condition)(trueBranch)(falseBranch)
