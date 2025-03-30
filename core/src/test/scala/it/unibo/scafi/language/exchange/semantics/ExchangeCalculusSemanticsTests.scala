package it.unibo.scafi.language.exchange.semantics

import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.UnitTest

trait ExchangeCalculusSemanticsTests:
  this: UnitTest =>

  def nvalues[C <: ExchangeCalculusSemantics & ExchangeCalculusSemanticsTestHelper](using lang: C): Unit =
    assume(lang.self == 0)
    assume(
      (0 until 10).toSet.subsetOf(lang.device.toSet),
      s"Devices: ${lang.device.toSet} is not a superset of ${0 until 10}",
    )
    val valuesMap: Map[Int, Int] = Map(0 -> 1, 1 -> 2, 2 -> 3, 3 -> 42, 4 -> 125)
    val nv = lang.mockNValues(10, valuesMap)
    it should "provide the default value" in:
      nv.default shouldEqual 10
    it should "allow to retrieve a value map" in:
      val v: Map[lang.DeviceId, Int] = nv.values.toMap
      v.values.toList should contain theSameElementsAs lang.device.toIterable
        .map(id => valuesMap.getOrElse(id, 10))
        .toList
    it should "allow to retrieve a value" in:
      nv.get(lang.self) shouldEqual 1
      nv(lang.self) shouldEqual 1
      nv.get(lang.unalignedDeviceId) shouldEqual 10
      nv(lang.unalignedDeviceId) shouldEqual 10
    it should "allow to override a value for an aligned device" in:
      val newNv = nv.set(lang.self, 100)
      newNv.get(lang.self) shouldEqual 100
    it should "not allow to override a value for an unaligned device" in:
      var newNv = nv.set(lang.self, 100)
      newNv.get(lang.unalignedDeviceId) shouldEqual 10
      newNv = newNv.set(lang.unalignedDeviceId, 100)
      newNv.get(lang.unalignedDeviceId) shouldEqual 10
  end nvalues

  def exchangeCalculusSemanticsWithAtLeast10AlignedDevices[
      C <: ExchangeCalculusSemantics & ExchangeCalculusSemanticsTestHelper,
  ](using lang: C): Unit =
    it should "provide conversion from local values to nvalues using the default value" in:
      "val _: lang.SharedData[Int] = 10" should compile
      val example: lang.SharedData[String] = "a"
      example(lang.self) shouldEqual "a"
      example(lang.unalignedDeviceId) shouldEqual "a"
    "NValues" should behave like nvalues[C]
end ExchangeCalculusSemanticsTests
