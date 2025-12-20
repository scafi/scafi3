package it.unibo.scafi.message

import it.unibo.scafi.message.ValueTree.NoPathFoundException
import it.unibo.scafi.utils.InvocationCoordinate

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

import unsafeExceptions.canThrowAny

class ValueTreeTest extends AnyFlatSpecLike, should.Matchers:
  given Conversion[String, InvocationCoordinate] = key => InvocationCoordinate(key, invocationCount = 0)

  private val emptyValueTree = ValueTree.empty
  private val valueTree = ValueTree(Map(Path("path1") -> "value1", Path("path2") -> "value2"))

  "ValueTree" should "not contain any paths when created via the empty method" in:
    emptyValueTree.paths shouldBe empty

  it should "raise a NoPathFoundException when trying to access a non-existing path" in:
    assertThrows[NoPathFoundException]:
      emptyValueTree.apply(Path("nonExistingPath"))

  it should "return None when trying to get a non-existing path" in:
    emptyValueTree.get(Path("nonExistingPath")) shouldBe None

  it should "create a ValueTree with the given paths and values" in:
    valueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"))
    valueTree.apply[String](Path("path1")) shouldBe "value1"
    valueTree.apply[String](Path("path2")) shouldBe "value2"

  it should "return Some(value) when trying to get an existing path" in:
    valueTree.get(Path("path1")) shouldBe Some("value1")
    valueTree.get(Path("path2")) shouldBe Some("value2")

  it should "update the value of an existing path" in:
    val updatedValueTree = valueTree.update(Path("path1"), "newValue")
    updatedValueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"))
    updatedValueTree.apply[String](Path("path1")) shouldBe "newValue"
    updatedValueTree.apply[String](Path("path2")) shouldBe "value2"

  it should "not affect the original ValueTree when updating" in:
    val updatedValueTree = valueTree.update(Path("path1"), "newValue")
    valueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"))
    valueTree.apply[String](Path("path1")) shouldBe "value1"
    valueTree.apply[String](Path("path2")) shouldBe "value2"
    updatedValueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"))
    updatedValueTree.apply[String](Path("path1")) shouldBe "newValue"

  it should "not affect the original ValueTree when updating a non-existing path" in:
    val updatedValueTree = valueTree.update(Path("path3"), "newValue")
    valueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"))
    valueTree.apply[String](Path("path1")) shouldBe "value1"
    valueTree.apply[String](Path("path2")) shouldBe "value2"
    assertThrows[NoPathFoundException]:
      valueTree(Path("path3"))
    updatedValueTree.paths should contain theSameElementsAs Seq(Path("path1"), Path("path2"), Path("path3"))
    updatedValueTree.apply[String](Path("path3")) shouldBe "newValue"

  it should "be equal to another ValueTree with the same paths and values" in:
    val anotherValueTree = ValueTree(Map(Path("path1") -> "value1", Path("path2") -> "value2"))
    valueTree should equal(anotherValueTree)

  it should "not be equal to another ValueTree with different paths or values" in:
    val anotherValueTree = ValueTree(Map(Path("path1") -> "value1", Path("path2") -> "differentValue"))
    valueTree should not equal anotherValueTree
end ValueTreeTest
