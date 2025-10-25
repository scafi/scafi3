package it.unibo.scafi.language.common

import it.unibo.scafi.language.common.calculus.BranchingCalculus
import it.unibo.scafi.language.common.syntax.BranchingSyntax

/**
 * Trait that enables the use of branching syntax in the aggregate programs.
 */
trait BranchingLanguage extends BranchingSyntax:
  this: BranchingCalculus =>

  override def branch[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T =
    br(condition)(trueBranch)(falseBranch)
