package it.unibo.field4s.alchemist.main

import it.unibo.field4s.alchemist.AlchemistContext
import it.unibo.field4s.language.libraries.All.{*, given}
import it.unibo.field4s.language.semantics.exchange
object TestProgram:

  def myProgram(using exchange.Language): Int = evolve(0)(_ + 1)

  def myProgram2(using exchange.Language & AlchemistContext[?]): Double =
    sensorDistanceTo(self == 0)
