package it.unibo.scafi.implementations

import it.unibo.scafi.UnitTest
import it.unibo.scafi.collections.{ ValueTreeFactoryTests, ValueTreeTests }
import it.unibo.scafi.implementations.collections.MapValueTree

class MapValueTreeTests extends UnitTest with ValueTreeTests with ValueTreeFactoryTests:

  val mapValueTree: MapValueTree[String, Int] = MapValueTree(
    underlying = Map(
      Seq("a", "b", "c", "d") -> 5,
      Seq("a", "b", "c", "e") -> 6,
      Seq("a", "b", "f") -> 7,
      Seq("a", "g") -> 8,
    ),
  )

  "A MapValueTree" should behave like nonEmptyValueTree(mapValueTree, _ * 5)

  "Its factory" should behave like valueTreeFactory(MapValueTree)
