package it.unibo.scafi.libraries
import it.unibo.scafi.types.PortableTypes

/**
 * The portable library providing domain branching syntax.
 */
trait PortableBranchingLibrary extends PortableLibrary:
  self: PortableTypes =>
  import it.unibo.scafi.language.common.syntax.BranchingSyntax

  override type Language <: AggregateFoundation & BranchingSyntax

  /**
   * This method is used to split the domain of the aggregate program into two branches.
   * @param condition
   *   the condition to be evaluated
   * @param trueBranch
   *   the expression to be evaluated if the condition is true
   * @param falseBranch
   *   the expression to be evaluated if the condition is false
   * @tparam Value
   *   the type of the expression to be evaluated
   * @return
   *   the result of the expression that has been evaluated
   */
  @JSExport
  def branch[Value](condition: Boolean)(trueBranch: Function0[Value])(falseBranch: Function0[Value]): Value =
    branch_(condition)(trueBranch)(falseBranch)

  inline def branch_[Value](condition: Boolean)(trueBranch: Function0[Value])(falseBranch: Function0[Value]): Value =
    language.branch(condition)(trueBranch())(falseBranch())
end PortableBranchingLibrary
