package it.unibo.field4s.abstractions

import it.unibo.field4s.UnitTest
import it.unibo.field4s.abstractions.BidirectionalFunction.<=>

class BidirectionalFunctionTests extends UnitTest:
  val x: Int <=> Double = BidirectionalFunction[Int, Double](_.toDouble, _.toInt)

  "Type operator" should "alias bidirectional functions" in:
    x shouldBe a[BidirectionalFunction[Int, Double]]

  "Identity" should "consist of a parameterless operator call" in:
    val id: Int <=> Int = <=>
    id.forward(1) should be(1)
    id.backward(7) should be(7)

  "Double argument operator" should "provide forward and backward" in:
    val x: Int <=> Double = <=>(_.toDouble, _.toInt)
    x.forward(1) should be(1.0)
    x.backward(7.0) should be(7)

  "Single argument operator" should "provide only forward" in:
    val y: Int <=> Int = <=>(_ * 2)
    y.forward(3) should be(6)
    y.backward(7) should be(7)
end BidirectionalFunctionTests
