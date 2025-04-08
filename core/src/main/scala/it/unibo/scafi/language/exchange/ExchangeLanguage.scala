package it.unibo.scafi.language.exchange

import it.unibo.scafi.language.exchange.semantic.{
  BranchingExchangeSemantic,
  ExchangeSemantic,
  FieldCalculusViaExchangeSemantic,
}
import it.unibo.scafi.language.exchange.calculus.ExchangeCalculus

trait ExchangeLanguage extends ExchangeSemantic, BranchingExchangeSemantic, FieldCalculusViaExchangeSemantic:
  self: ExchangeCalculus =>
