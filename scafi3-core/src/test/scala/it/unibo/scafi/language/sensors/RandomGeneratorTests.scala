package it.unibo.scafi.language.sensors

import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.sensors.RandomGenerator

class RandomGeneratorTests extends UnitTest:

  type Language = AggregateFoundation & RandomGenerator

  val rng: scala.util.Random = new scala.util.Random(seed = 12345L)

  given lang: Language = new AggregateFoundationMock with RandomGenerator:
    override def nextRandom: Double = rng.nextDouble()

  "RandomGenerator.nextRandom" should "delegate to the foundation" in:
    val r1 = lang.nextRandom
    val r2 = RandomGenerator.nextRandom
    // both calls advance the same rng; they should be different draws but both in [0,1)
    r1 should (be >= 0.0 and be < 1.0)
    r2 should (be >= 0.0 and be < 1.0)

  it should "return values in [0, 1)" in:
    for _ <- 1 to 100 do
      val v = RandomGenerator.nextRandom
      v should (be >= 0.0 and be < 1.0)

  it should "advance the stream across rounds (successive draws differ)" in:
    val a = RandomGenerator.nextRandom
    val b = RandomGenerator.nextRandom
    a should not equal b
end RandomGeneratorTests
