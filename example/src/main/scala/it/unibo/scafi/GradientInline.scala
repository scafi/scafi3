package it.unibo.scafi

import it.unibo.alchemist.boundary.LoadAlchemist

object GradientInline extends App:
  val simulationFile = getClass.getResource("/it/unibo/scafi/inline-gradient.yml").getPath
  val loader = LoadAlchemist.from(simulationFile)
  val simulation = loader.getDefault[Any, Nothing]()
  simulation.run()
