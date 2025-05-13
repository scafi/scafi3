package it.unibo.scafi.language.common.calculus

trait BranchingCalculus:
  def br[T](condition: Boolean)(th: => T)(el: => T): T
