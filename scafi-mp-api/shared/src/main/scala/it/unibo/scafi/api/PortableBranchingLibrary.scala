package it.unibo.scafi.api

/**
 * The portable library providing domain branching syntax.
 */
trait PortableBranchingLibrary extends PortableLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language.common.syntax.BranchingSyntax

  @JSExport
  def branch[T](using
      language: AggregateFoundation & BranchingSyntax,
  )(condition: Boolean)(thenBranch: => T)(elseBranch: => T): T =
    language.branch(condition)(thenBranch)(elseBranch)
