package it.unibo.field4s.language

import it.unibo.field4s.language.bindings.exchange.{
  BranchingExchangeBindings,
  ExchangeBindings,
  FieldCalculusByExchangeBindings,
}
import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics

trait ExchangeLanguage extends ExchangeBindings, BranchingExchangeBindings, FieldCalculusByExchangeBindings:
  self: ExchangeCalculusSemantics =>
