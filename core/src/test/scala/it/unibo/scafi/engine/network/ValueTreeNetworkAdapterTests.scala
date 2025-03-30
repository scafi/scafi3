package it.unibo.scafi.engine.network

import it.unibo.scafi.engine.context.ValueTreeTestingNetwork
import it.unibo.scafi.UnitTest
import it.unibo.scafi.collections.{ MapWithDefault, ValueTree }
import it.unibo.scafi.abstractions.BidirectionalFunction.<=>

class ValueTreeNetworkAdapterTests extends UnitTest:

  val network: ValueTreeTestingNetwork[Int, String, Int] = ValueTreeTestingNetwork(
    localId = 10,
    received = Map(
      1 -> ValueTree(
        Seq("a", "b", "c") -> 10,
      ),
      2 -> ValueTree(
        Seq("d", "e", "f") -> 20,
      ),
    ),
  )

  val adaptedNetwork: Network[String, ValueTree[String, Double]] = ValueTreeNetworkAdapter(network)
    .byDeviceId(<=>(_.toString, _.toInt))
    .byToken(<=>(_ + "kek", _.dropRight(3)))
    .byValue(<=>(_.toDouble, _.toInt))

  "Adapted network" should "return mapped local id" in:
    adaptedNetwork.localId shouldBe "10"

  it should "return mapped received" in:
    adaptedNetwork.receive().view.mapValues(_.toMap).toMap shouldBe Map(
      "1" -> Map(
        Seq("akek", "bkek", "ckek") -> 10.toDouble,
      ),
      "2" -> Map(
        Seq("dkek", "ekek", "fkek") -> 20.toDouble,
      ),
    )

  it should "send reverse mapped messages" in:
    adaptedNetwork.send(
      MapWithDefault(
        default = ValueTree.empty,
        underlying = Map(
          "5" -> ValueTree(
            Seq("gkek", "hkek", "ikek") -> 30.toDouble,
          ),
        ),
      ),
    )
    network.sent.mapValues(_.toMap).toMap shouldBe Map(
      5 -> Map(
        Seq("g", "h", "i") -> 30.toDouble.toInt,
      ),
    )
end ValueTreeNetworkAdapterTests
