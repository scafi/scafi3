package it.unibo.scafi.language.exchange

import it.unibo.scafi.language.exchange.bindings.{
  BranchingExchangeBindings,
  ExchangeBindings,
  FieldCalculusByExchangeBindings,
}
import it.unibo.scafi.language.exchange.calculus.ExchangeCalculus

trait ExchangeLanguage extends ExchangeBindings, BranchingExchangeBindings, FieldCalculusByExchangeBindings:
  self: ExchangeCalculus =>
