package it.unibo.field4s.language.exchange

import it.unibo.field4s.language.exchange.bindings.{BranchingExchangeBindings, ExchangeBindings, FieldCalculusByExchangeBindings}
import it.unibo.field4s.language.exchange.semantics.ExchangeCalculusSemantics

trait ExchangeLanguage extends ExchangeBindings, BranchingExchangeBindings, FieldCalculusByExchangeBindings:
  self: ExchangeCalculusSemantics =>
