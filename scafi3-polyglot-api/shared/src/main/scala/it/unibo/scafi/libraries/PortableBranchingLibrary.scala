package it.unibo.scafi.libraries
import it.unibo.scafi.types.PortableTypes

/**
 * The portable library providing domain branching syntax.
 */
trait PortableBranchingLibrary extends PortableLibrary:
  self: PortableTypes =>
  import it.unibo.scafi.language.common.syntax.BranchingSyntax

  override type Language <: AggregateFoundation & BranchingSyntax

  @JSExport
  def branch[Value](condition: Boolean)(trueBranch: Function0[Value])(falseBranch: Function0[Value]): Value
