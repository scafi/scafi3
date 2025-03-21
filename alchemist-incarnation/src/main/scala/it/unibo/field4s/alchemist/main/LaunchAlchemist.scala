package it.unibo.field4s.alchemist.main

import it.unibo.alchemist.model.terminators.AfterTime
import it.unibo.alchemist.test.EuclideanSimulationKt
import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.alchemist.model.times.DoubleTime

@main def main(): Unit =
  val sim = EuclideanSimulationKt
    .loadYamlSimulation[Any, Euclidean2DPosition](
      "simulation-gradient.yml",
      java.util.Map.of(),
    )
  sim.getEnvironment.nn.addTerminator(AfterTime[Any, Euclidean2DPosition](DoubleTime(100000.0)))
  sim.run()
