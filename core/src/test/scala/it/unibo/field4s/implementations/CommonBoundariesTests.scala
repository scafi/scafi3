package it.unibo.field4s.implementations

import it.unibo.field4s.UnitTest
import it.unibo.field4s.abstractions.BoundedTests
import it.unibo.field4s.implementations.CommonBoundaries.given

class CommonBoundariesTests extends UnitTest with BoundedTests:
  "Double" should behave like bounded[Double]()
  "Float" should behave like bounded[Float]()
