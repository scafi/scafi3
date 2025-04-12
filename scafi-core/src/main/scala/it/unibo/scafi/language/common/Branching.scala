package it.unibo.scafi.language.common

trait Branching:
  def br[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T
