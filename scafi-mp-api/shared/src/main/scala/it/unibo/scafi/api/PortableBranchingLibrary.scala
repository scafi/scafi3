package it.unibo.scafi.api

/**
 * The portable library providing domain branching syntax.
 */
trait PortableBranchingLibrary extends PortableLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language.common.syntax.BranchingSyntax

  override type Language <: AggregateFoundation & BranchingSyntax

  @JSExport
  def branch[T](condition: Boolean)(thenBranch: => T)(elseBranch: => T): T =
    language.branch(condition)(thenBranch)(elseBranch)
