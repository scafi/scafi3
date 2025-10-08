package it.unibo.scafi.libraries

import it.unibo.scafi.language.common.syntax.BranchingSyntax

/**
 * This library provides domain branching syntax.
 */
object BranchingLibrary:

  /**
   * This method is used to split the domain of the aggregate program into two branches. The condition is evaluated and
   * if it is true the first expression is evaluated, otherwise the second expression is evaluated.
   * @param cond
   *   the condition to be evaluated
   * @param th
   *   the expression to be evaluated if the condition is true
   * @param el
   *   the expression to be evaluated if the condition is false
   * @tparam T
   *   the type of the expression to be evaluated
   * @return
   *   the result of the expression that has been evaluated
   *
   * @see
   *   [[BranchingSyntax.branch]]
   */
  def branch[T](using language: BranchingSyntax)(cond: Boolean)(th: => T)(el: => T): T =
    language.branch(cond)(th)(el)
end BranchingLibrary
