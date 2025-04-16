package it.unibo.scafi.message

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ImportTest extends AnyFlatSpecLike, should.Matchers:
  "Import" should "be empty when created via the empty method" in:
    val emptyImport = Import.empty[Int]
    emptyImport.neighbors shouldBe empty
    emptyImport.isEmpty shouldBe true

  it should "return the set of neighbors" in:
    val messages = Map(1 -> ValueTree(Map()), 2 -> ValueTree(Map()))
    val importInstance = Import(messages)
    importInstance.neighbors should contain theSameElementsAs Set(1, 2)
