package it.unibo.field4s.alchemist.main

import it.unibo.field4s.alchemist.AlchemistContext
import it.unibo.field4s.language.semantics.exchange.ExchangeCalculusSemantics
import it.unibo.field4s.language.libraries.All.{ *, given }

object TestProgram:

  def myProgram(using ExchangeCalculusSemantics): Int = evolve(0)(_ + 1)

  def myProgram2(using AlchemistContext[?]): Double =
    sensorDistanceTo(self == 0)
