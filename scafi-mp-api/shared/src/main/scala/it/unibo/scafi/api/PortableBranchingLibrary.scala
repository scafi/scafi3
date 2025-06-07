package it.unibo.scafi.api

import it.unibo.scafi.language.common.syntax.BranchingSyntax

trait PortableBranchingLibrary extends PortableLibrary:
  ctx: PortableTypes =>

  override type Language <: AggregateFoundation & BranchingSyntax

  def branch[T](using language: Language)(condition: Boolean)(thenBranch: => T)(elseBranch: => T): T =
    language.branch(condition)(thenBranch)(elseBranch)
