package it.unibo.scafi.language.exchange

import it.unibo.scafi.language.exchange.bindings.{
  BranchingExchangeBindings,
  ExchangeBindings,
  FieldCalculusByExchangeBindings,
}
import it.unibo.scafi.language.exchange.semantics.ExchangeCalculusSemantics

trait ExchangeLanguage extends ExchangeBindings, BranchingExchangeBindings, FieldCalculusByExchangeBindings:
  self: ExchangeCalculusSemantics =>
