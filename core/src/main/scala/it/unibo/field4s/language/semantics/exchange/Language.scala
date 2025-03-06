package it.unibo.field4s.language.semantics.exchange

type Language = ExchangeCalculusSemantics & Binding

trait Binding
    extends bindings.ExchangeBindings
    with bindings.BranchingExchangeBindings
    with bindings.FieldCalculusByExchangeBindings:
  self: ExchangeCalculusSemantics =>

