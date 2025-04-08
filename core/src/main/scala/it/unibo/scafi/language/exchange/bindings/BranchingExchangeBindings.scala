package it.unibo.scafi.language.exchange.bindings

import it.unibo.scafi.language.exchange.calculus.ExchangeCalculus
import it.unibo.scafi.language.syntax.BranchingSyntax

/**
 * This trait witnesses the fact that the exchange calculus semantics can be used to implement the branching syntax.
 */
trait BranchingExchangeBindings extends BranchingSyntax:
  self: ExchangeCalculus =>

  override def branch[T](condition: Boolean)(trueBranch: => T)(falseBranch: => T): T =
    br(condition)(trueBranch)(falseBranch)
