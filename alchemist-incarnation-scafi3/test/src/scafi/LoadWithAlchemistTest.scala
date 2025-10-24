package it.unibo.scafi

import it.unibo.alchemist.boundary.LoadAlchemist
import it.unibo.alchemist.model.terminators.AfterTime
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.util.ClassPathScanner
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LoadWithAlchemistTest extends AnyFlatSpec, Matchers:

  "A simulation" should "be loaded and executed from a resource folder" in:
    ClassPathScanner
      .resourcesMatching(".*\\.ya?ml", "it.unibo.scafi")
      .forEach: simulationFile =>
        val loader = LoadAlchemist.from(simulationFile)
        loader shouldNot be(null) // scalafix:ok
        val simulation = loader.getDefault[Any, Nothing]()
        simulation shouldNot be(null) // scalafix:ok
        simulation.getEnvironment.addTerminator(AfterTime(DoubleTime(10.0)))
        val _ = simulation.play()
        simulation.run()
