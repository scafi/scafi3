package it.unibo.scafi.language.xc

import it.unibo.scafi.language.xc.semantic.{
  BranchingExchangeSemantic,
  ExchangeSemantic,
  FieldCalculusViaExchangeSemantic,
}
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus

trait ExchangeLanguage extends ExchangeSemantic, BranchingExchangeSemantic, FieldCalculusViaExchangeSemantic:
  self: ExchangeCalculus =>
