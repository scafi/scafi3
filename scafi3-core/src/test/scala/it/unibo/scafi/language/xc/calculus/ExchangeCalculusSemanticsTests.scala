package it.unibo.scafi.language.xc.calculus

import it.unibo.scafi.UnitTest
import it.unibo.scafi.collections.SafeIterable
import it.unibo.scafi.message.ValueTree
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.test.network.NeighborsNetworkManager

import cats.implicits.catsSyntaxTuple2Semigroupal

trait ExchangeCalculusSemanticsTests:
  this: UnitTest =>

  def nvalues[Context <: ExchangeCalculus & ExchangeCalculusSemanticsTestHelper](
      contextFactory: (NetworkManager { type DeviceId = Int }, ValueTree) => Context,
  ): Unit =
    val lang = contextFactory(
      NeighborsNetworkManager[Int](localId = 0, (0 until 10).toSet),
      ValueTree.empty,
    )
    val devices = (0 until 10).toSet
    assume(lang.localId == 0)
    assume(
      devices.subsetOf(lang.device.toSet),
      s"Devices: ${lang.device.toSet} is not a superset of ${0 until 10}",
    )
    val valuesMap: Map[Int, Int] = Map(0 -> 1, 1 -> 2, 2 -> 3, 3 -> 42, 4 -> 125)
    val nv = lang.mockSharedData(10, devices, valuesMap)
    it should "provide the default value" in:
      nv.default shouldEqual 10
    it should "allow to retrieve a value map" in:
      val v: SafeIterable[Int] = nv.withoutSelf
      v.toList should contain theSameElementsAs lang.device.toIterable
        .filterNot(_ == lang.localId)
        .map(id => valuesMap.getOrElse(id, 10))
        .toList
    it should "allow to retrieve a value" in:
      nv(lang.localId) shouldEqual 1
      nv(lang.localId) shouldEqual 1
      nv(lang.unalignedDeviceId) shouldEqual 10
      nv(lang.unalignedDeviceId) shouldEqual 10
    it should "allow to override a value for an aligned device" in:
      val newNv = nv.set(lang.localId, 100)
      newNv(lang.localId) shouldEqual 100
    it should "not allow to override a value for an unaligned device" in:
      var newNv = nv.set(lang.localId, 100)
      newNv(lang.unalignedDeviceId) shouldEqual 10
      newNv = newNv.set(lang.unalignedDeviceId, 100)
      newNv(lang.unalignedDeviceId) shouldEqual 10
    it should "consider only aligned devices (and use default for unaligned) when mapping from multiple fields" in:
      val f1 = lang.mockSharedData(1, devices, Map(1 -> 20))
      val f2 = lang.mockSharedData(2, devices, Map(0 -> 10))
      val mapped = (f1, f2).mapN(_ + _)
      mapped should be(lang.mockSharedData(3, devices, Map(0 -> 11, 1 -> 22) ++ (2 until 10).map(id => id -> 3).toMap))
  end nvalues

// Replace below with simulator
//  def exchangeCalculusSemanticsWithAtLeast10AlignedDevices[
//      C <: ExchangeCalculus & ExchangeCalculusSemanticsTestHelper,
//  ](using lang: C): Unit =
//    it should "provide conversion from local values to nvalues using the default value" in:
//      "val _: lang.SharedData[Int] = 10" should compile
//      val example: lang.SharedData[String] = "a"
//      example(lang.localId) shouldEqual "a"
//      example(lang.unalignedDeviceId) shouldEqual "a"
//    "SharedData" should behave like nvalues[C]
end ExchangeCalculusSemanticsTests
