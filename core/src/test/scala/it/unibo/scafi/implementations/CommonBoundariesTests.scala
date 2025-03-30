package it.unibo.scafi.implementations

import it.unibo.scafi.UnitTest
import it.unibo.scafi.abstractions.BoundedTests
import it.unibo.scafi.implementations.CommonBoundaries.given

class CommonBoundariesTests extends UnitTest with BoundedTests:
  "Double" should behave like bounded[Double]()
  "Float" should behave like bounded[Float]()
