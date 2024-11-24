package it.unibo.field4s.language.syntax

import it.unibo.field4s.language.foundation.AggregateFoundation

/**
 * This trait enables the use of branching syntax in the aggregate programs. The syntax is similar to the `if` statement
 * in Scala.
 */
trait BranchingSyntax:
  self: AggregateFoundation =>

  /**
   * This method is used to split the domain of the aggregate program into two branches.
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
   */
  def branch[T](cond: Boolean)(th: => T)(el: => T): T
end BranchingSyntax
