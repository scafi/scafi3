package it.unibo.scafi

import it.unibo.alchemist.boundary.LoadAlchemist

object GradientInline:
  @main
  def runInline(): Unit =
    val simulationFile = getClass.getResource("/it/unibo/scafi/inline-gradient.yml")
    val loader = LoadAlchemist.from(simulationFile)
    val simulation = loader.getDefault[Any, Nothing]()
    simulation.run()
