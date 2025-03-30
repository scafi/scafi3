package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest

class MapWithDefaultTests extends UnitTest:
  val emptyMapWithDefault: MapWithDefault[String, Int] = MapWithDefault(Map.empty, 5)
  val nonEmptyMapWithDefault: MapWithDefault[String, Int] = MapWithDefault(Map("a" -> 1, "b" -> 42), 5)
  val newElement: (String, Int) = "x" -> 111

  val nonEmptyMapWithDefaultWithNewElement: MapWithDefault[String, Int] =
    MapWithDefault(Map("a" -> 1, "b" -> 42, "x" -> 111), 5)
  val nonEmptyMap2: IterableOnce[(String, Int)] = Map("a" -> 2, "c" -> 420)
  val nonEmptyMap1plus2: MapWithDefault[String, Int] = MapWithDefault(Map("a" -> 2, "b" -> 42, "c" -> 420), 5)

  "A MapWithDefault" should "return the default value when the key is not present" in:
    emptyMapWithDefault("a") shouldBe 5

  it should "have a different hash code for different default values" in:
    val map1 = MapWithDefault[Int, Int](Map.empty, 1)
    val map2 = MapWithDefault[Int, Int](Map.empty, 2)
    map1.hashCode should not be map2.hashCode

  it should "have same hash code for same default values" in:
    val map1 = MapWithDefault[Int, Int](Map(2 -> 2), 1)
    val map2 = MapWithDefault[Int, Int](Map(2 -> 2), 1)
    map1.hashCode shouldBe map2.hashCode

  it should "be iterable" in:
    emptyMapWithDefault.isEmpty shouldBe true
    nonEmptyMapWithDefault.isEmpty shouldBe false
    nonEmptyMapWithDefault.size shouldBe 2
    nonEmptyMapWithDefault shouldBe an[Iterable[(String, Int)]]

  it should "provide an infix ++ operator" in:
    nonEmptyMapWithDefault ++ nonEmptyMap2 shouldBe nonEmptyMap1plus2

  it should "provide an infix + operator" in:
    nonEmptyMapWithDefault + newElement shouldBe nonEmptyMapWithDefaultWithNewElement

  it should "provide an infix - operator" in:
    nonEmptyMapWithDefaultWithNewElement - newElement._1 shouldBe nonEmptyMapWithDefault

  it should "provide an infix -- operator" in:
    nonEmptyMapWithDefaultWithNewElement -- List(newElement._1) shouldBe nonEmptyMapWithDefault

  it should "allow concatenation" in:
    nonEmptyMapWithDefault.concat(nonEmptyMap2) shouldBe nonEmptyMap1plus2

  it should "provide the default value" in:
    nonEmptyMapWithDefault.default shouldBe 5

  it should "provide an empty generator that preserves default" in:
    nonEmptyMapWithDefault.empty shouldBe MapWithDefault.empty[String, Int](nonEmptyMapWithDefault.default)

  it should "allow filtering key values" in:
    nonEmptyMapWithDefault.filter(_._1 == "a") shouldBe MapWithDefault(Map("a" -> 1), 5)

  it should "allow filtering key values with a negated predicate" in:
    nonEmptyMapWithDefault.filterNot(_._2 == 1) shouldBe MapWithDefault(Map("b" -> 42), 5)

  it should "allow flat mapping while preserving the default value" in:
    nonEmptyMapWithDefault.flatMap((k, v) => List(k -> v * 2, k + "bis" -> v * 3)) shouldBe MapWithDefault(
      Map("a" -> 2, "abis" -> 3, "b" -> 84, "bbis" -> 126),
      5,
    )

  it should "allow retrieving a value using the default in case" in:
    nonEmptyMapWithDefault.get("a") shouldBe 1
    nonEmptyMapWithDefault.get("x") shouldBe nonEmptyMapWithDefault.default

  it should "allow mapping values while mapping the default value" in:
    nonEmptyMapWithDefault.mapValues(v => v * 4) shouldBe MapWithDefault(Map("a" -> 4, "b" -> 168), 20)

  it should "allow mapping keys without altering the default value" in:
    nonEmptyMapWithDefault.mapKeys(k => k + "bis") shouldBe MapWithDefault(Map("abis" -> 1, "bbis" -> 42), 5)

  it should "allow partitioning to same type" in:
    val (even, odd) = nonEmptyMapWithDefault.partition(_._2 % 2 == 0)
    even shouldBe MapWithDefault(Map("b" -> 42), 5)
    odd shouldBe MapWithDefault(Map("a" -> 1), 5)

  it should "allow removing a key" in:
    nonEmptyMapWithDefaultWithNewElement.removed(newElement._1) shouldBe nonEmptyMapWithDefault

  it should "allow removing multiple keys" in:
    nonEmptyMapWithDefaultWithNewElement.removedAll(List(newElement._1)) shouldBe nonEmptyMapWithDefault

  it should "allow tapping each tuple" in:
    nonEmptyMapWithDefault.tapEach(_ => ()) shouldBe theSameInstanceAs(nonEmptyMapWithDefault)

  it should "allow updating/inserting a key value" in:
    nonEmptyMapWithDefault.updated("a", 77) shouldBe MapWithDefault(Map("a" -> 77, "b" -> 42), 5)
    nonEmptyMapWithDefault.updated(newElement._1, newElement._2) shouldBe nonEmptyMapWithDefaultWithNewElement

  it should "allow updating/removing a value based on the previous" in:
    nonEmptyMapWithDefault.updatedWith("a")(_ => None) shouldBe MapWithDefault(Map("b" -> 42), 5)
    nonEmptyMapWithDefault.updatedWith("a")(prev => Some(prev + 10)) shouldBe MapWithDefault(
      Map("a" -> 11, "b" -> 42),
      5,
    )
    nonEmptyMapWithDefault.updatedWith("x")(prev => Some(prev + 10)) shouldBe MapWithDefault(
      Map("a" -> 1, "b" -> 42, "x" -> 15),
      5,
    )

  it should "allow changing the default value" in:
    nonEmptyMapWithDefault.withDefault(10) shouldBe MapWithDefault(Map("a" -> 1, "b" -> 42), 10)

  it should "provide a MapView" in:
    nonEmptyMapWithDefault.view.toIndexedSeq shouldBe nonEmptyMapWithDefault.toMap.view.toIndexedSeq

  it should "provide an empty builder" in:
    MapWithDefault.empty[Int, String]("ok") shouldBe MapWithDefault(Map.empty[Int, String], "ok")
end MapWithDefaultTests
