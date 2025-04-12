package it.unibo.scafi.language.xc

import it.unibo.scafi.language.common.Branching
import it.unibo.scafi.language.common.semantic.BranchingSemantic
import it.unibo.scafi.language.xc.semantic.{ExchangeSemantic, FieldCalculusViaExchangeSemantic}
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus

trait ExchangeLanguage extends ExchangeSemantic, BranchingSemantic, FieldCalculusViaExchangeSemantic:
  self: ExchangeCalculus & Branching =>
