package it.unibo.scafi.message

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ExportTest extends AnyFlatSpecLike, should.Matchers:
  private val defaultValue: ValueTree = ValueTree(
    Map(
      Path("key1") -> "default1",
      Path("key2") -> "default2",
      Path("key3") -> "default3",
    ),
  )
  private val overriddenValue: ValueTree = ValueTree(
    Map(
      Path("key1") -> "overridden1",
      Path("key2") -> "overridden2",
      Path("key3") -> "overridden3",
    ),
  )
  "An Export object" should "be created from a default ValueTree and empty overrides" in:
    val exp = Export[Int](defaultValue, Map.empty)
    exp(0) shouldBe defaultValue

  it should "returns the default ValueTree when deviceId is not present in the overrides" in:
    val exp = Export[Int](defaultValue, Map.empty)
    exp(1) shouldBe defaultValue

  it should "returns the overridden ValueTree when deviceId is present in the overrides" in:
    val exp = Export[Int](defaultValue, Map(1 -> overriddenValue))
    exp(1) shouldBe overriddenValue
    exp(0) shouldBe defaultValue

  it should "be not equal to another Export with different default ValueTree" in:
    val export1 = Export[Int](defaultValue, Map(1 -> overriddenValue))
    val export2 = Export[Int](ValueTree(Map(Path("key4") -> "newDefault")), Map(1 -> overriddenValue))
    export1 should not equal export2

  it should "be not equal to another Export with different overrides" in:
    val export1 = Export[Int](defaultValue, Map(1 -> overriddenValue))
    val export2 = Export[Int](defaultValue, Map(2 -> overriddenValue))
    export1 should not equal export2

  it should "be equal to another Export with the same default ValueTree and overrides" in:
    val export1 = Export[Int](defaultValue, Map(1 -> overriddenValue))
    val export2 = Export[Int](defaultValue, Map(1 -> overriddenValue))
    export1 should equal(export2)
end ExportTest
