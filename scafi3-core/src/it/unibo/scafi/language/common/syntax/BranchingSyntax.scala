package it.unibo.scafi.language.common.syntax

/**
 * This trait enables the use of branching syntax in the aggregate programs. The syntax is similar to the `if` statement
 * in Scala.
 */
trait BranchingSyntax:

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
  def branch[Value](condition: Boolean)(trueBranch: => Value)(falseBranch: => Value): Value
end BranchingSyntax
