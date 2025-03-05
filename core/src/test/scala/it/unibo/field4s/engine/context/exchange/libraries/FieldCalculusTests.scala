package it.unibo.field4s.engine.context.exchange.libraries

import it.unibo.field4s.engine.context.{ ValueTreeProbingContextMixin, ValueTreeTestingNetwork }
import it.unibo.field4s.UnitTest
import it.unibo.field4s.collections.{ MapWithDefault, ValueTree }
import it.unibo.field4s.engine.context.ContextFactory
import it.unibo.field4s.engine.context.common.InvocationCoordinate
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.{ Export, Import }
import it.unibo.field4s.language.libraries.All.{ *, given }

trait FieldCalculusTests:
  this: UnitTest & ValueTreeProbingContextMixin & BasicFactoryMixin =>

  def nbrSemantics(): Unit =
    var neighbours: Set[Int] = Set.empty
    def neighbouringProgram(using BasicExchangeCalculusContext[Int]): Unit =
      neighbours = neighborValues(self + 5).toSet

    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 66,
      factory = factory,
      program = neighbouringProgram,
    )

    it should "send the value to self without neighbours after boot" in:
      exportProbe.single._1 shouldBe 66
      exportProbe.single._2.single._2.as[Int] shouldBe 71
      neighbours shouldBe Set(71)

    it should "send the value to self without neighbours" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = neighbouringProgram,
        inboundMessages = Map(66 -> exportProbe(66)),
      )
      exportProbe.single._1 shouldBe 66
      exportProbe.single._2.single._2.as[Int] shouldBe 71
      neighbours shouldBe Set(71)

    it should "send the value to self with neighbours" in:
      exportProbe = probe(
        localId = 1,
        factory = factory,
        program = neighbouringProgram,
        inboundMessages = Map(
          1 -> probe(
            localId = 1,
            factory = factory,
            program = neighbouringProgram,
          )(1),
          2 -> probe(
            localId = 2,
            factory = factory,
            program = neighbouringProgram,
          )(1),
          3 -> probe(
            localId = 3,
            factory = factory,
            program = neighbouringProgram,
          )(1),
        ),
      )
      exportProbe.size shouldBe 3
      exportProbe(1).single._2.as[Int] shouldBe 6
      neighbours shouldBe Set(6, 7, 8)

    it should "always send the result of the expression to neighbours" in:
      exportProbe = probe(
        localId = 1,
        factory = factory,
        program = neighbouringProgram,
        inboundMessages = Map(
          2 -> probe(
            localId = 2,
            factory = factory,
            program = neighbouringProgram,
          )(1),
        ),
      )
      exportProbe(2).single._2.as[Int] shouldBe 6
  end nbrSemantics

  def repSemantics(): Unit =
    var last: Int = 0
    def repeatingProgram(using BasicExchangeCalculusContext[Int]): Unit =
      last = evolve(0)(_ + 2)

    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 66,
      factory = factory,
      program = repeatingProgram,
    )
    val messageFromNeighbour: Import[Int, BasicExchangeCalculusContext.ExportValue] = Map(
      1 ->
        probe( // adding a neighbour should not alter the result
          localId = 1,
          factory = factory,
          program = repeatingProgram,
        )(66),
    )

    it should "start from init after boot" in:
      exportProbe.single._1 shouldBe 66
      exportProbe(66).single._2.as[Option[Int]] shouldBe Some(2)
      last shouldBe 2

    it should "remember the last value after every iteration, and send None to others" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = repeatingProgram,
        inboundMessages = messageFromNeighbour + (66 -> exportProbe(66)),
      )
      exportProbe.keySet should contain(66)
      exportProbe(66).single._2.as[Option[Int]] shouldBe Some(4)
      exportProbe(1).single._2.as[Option[Int]] shouldBe None
      last shouldBe 4
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = repeatingProgram,
        inboundMessages = messageFromNeighbour + (66 -> exportProbe(66)),
      )
      exportProbe.keySet should contain(66)
      exportProbe(66).single._2.as[Option[Int]] shouldBe Some(6)
      exportProbe(1).single._2.as[Option[Int]] shouldBe None
      last shouldBe 6

    it should "restart after reboot" in:
      exportProbe = probe(
        localId = 66,
        factory = factory,
        program = repeatingProgram,
        inboundMessages = messageFromNeighbour,
      )
      exportProbe.keySet should contain(66)
      exportProbe(66).single._2.as[Option[Int]] shouldBe Some(2)
      exportProbe(1).single._2.as[Option[Int]] shouldBe None
      last shouldBe 2
  end repSemantics

  def shareSemantics(): Unit =
    var res: Int = 0
    def sharingProgram(using BasicExchangeCalculusContext[Int]): Unit =
      res = share(1)(_.sum)
    var exportProbe: Export[Int, BasicExchangeCalculusContext.ExportValue] = probe(
      localId = 7,
      factory = factory,
      program = sharingProgram,
    )
    it should "start from init after boot" in:
      exportProbe.single._1 shouldBe 7
      exportProbe.single._2.single._2.as[Int] shouldBe 1
      res shouldBe 1
    it should "send the value to self without neighbours" in:
      exportProbe = probe(
        localId = 7,
        factory = factory,
        program = sharingProgram,
        inboundMessages = Map(7 -> exportProbe(7)),
      )
      exportProbe.single._1 shouldBe 7
      exportProbe.single._2.single._2.as[Int] shouldBe 1
      res shouldBe 1
    it should "send/receive values to/from neighbours" in:
      exportProbe = probe(
        localId = 7,
        factory = factory,
        program = sharingProgram,
        inboundMessages = Map(
          7 -> exportProbe(7),
          1 -> probe(
            localId = 1,
            factory = factory,
            program = sharingProgram,
          )(7),
          2 -> probe(
            localId = 2,
            factory = factory,
            program = sharingProgram,
          )(7),
          3 -> probe(
            localId = 3,
            factory = factory,
            program = sharingProgram,
          )(7),
        ),
      )
      exportProbe.size shouldBe 4
      exportProbe(1).single._2.as[Int] shouldBe 4
      exportProbe(2).single._2.as[Int] shouldBe 4
      exportProbe = probe(
        localId = 7,
        factory = factory,
        program = sharingProgram,
        inboundMessages = Map(
          7 -> exportProbe(7),
          1 -> probe(
            localId = 1,
            factory = factory,
            program = sharingProgram,
          )(7),
          2 -> probe(
            localId = 2,
            factory = factory,
            program = sharingProgram,
          )(7),
          3 -> probe(
            localId = 3,
            factory = factory,
            program = sharingProgram,
          )(7),
        ),
      )
      exportProbe.size shouldBe 4
      exportProbe(1).single._2.as[Int] shouldBe 7
      exportProbe(2).single._2.as[Int] shouldBe 7

  end shareSemantics

  def fieldCalculusSemantics(): Unit =
    "nbr" should behave like nbrSemantics()
    "rep" should behave like repSemantics()
    "share" should behave like shareSemantics()
end FieldCalculusTests
