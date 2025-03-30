package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest

trait ValueTreeTests:
  this: UnitTest =>

  def nonEmptyValueTree[N, V](vt: ValueTree[N, V], transform: V => V)(using CanEqual[N, N], CanEqual[V, V]): Unit =
    val paths = vt.toMap.keySet
    val path = paths.head
    val otherPath = paths.find(_ != path).get
    val pathPrefix = path.take(2)
    val missingPath = path ++ path
    assume(path.length > 2)
    assume(transform(vt.get(path).get) != vt.get(path).get)
    assume(!paths.contains(missingPath))
    it should "allow to check for a contained path" in:
      vt.contains(path) should be(true)
      vt.contains(missingPath) should be(false)
    it should "allow to check for a contained prefix" in:
      vt.containsPrefix(path) shouldBe true
      vt.containsPrefix(pathPrefix) shouldBe true
      vt.containsPrefix(missingPath) shouldBe false
    it should "allow to retrieve the value for a contained path" in:
      vt.get(path) shouldBe defined
      vt.get(missingPath) shouldBe empty
    it should "allow to map just the values of the tree" in:
      val mapped = vt.mapValues((_, v) => v.toString)
      mapped.get(path) shouldBe defined
      mapped.get(path).get shouldBe vt.get(path).get.toString
    it should "allow to map just the nodes of the tree" in:
      val mapped = vt.mapNodes(_.toString)
      mapped.get(path.map(_.toString)) shouldBe defined
      mapped.get(path.map(_.toString)).get shouldBe vt.get(path).get
    it should "allow to map both the nodes and the values of the tree" in:
      val mapped = vt.map((n, v) => (n.map(_.toString), v.toString))
      mapped.get(path.map(_.toString)) shouldBe defined
      mapped.get(path.map(_.toString)).get shouldBe vt.get(path).get.toString
    it should "allow to filter the tree" in:
      val filtered = (vt + (missingPath -> vt(path))).filter((p, _) => p.length == path.length)
      filtered.get(path) shouldBe defined
      filtered.get(missingPath) shouldBe empty
    it should "allow to filter with negated predicate" in:
      val filtered = (vt + (missingPath -> vt(path))).filterNot((p, _) => p.length == path.length)
      filtered.get(path) shouldBe empty
      filtered.get(missingPath) shouldBe defined
    it should "allow to flatMap the tree" in:
      val flatMapped = vt.flatMap((p, v) => if p == path then Some(missingPath -> v) else None)
      flatMapped.get(path) shouldBe empty
      flatMapped.get(missingPath) shouldBe defined
      flatMapped.get(missingPath).get shouldBe vt.get(path).get
    it should "allow to remove a path if present" in:
      vt.remove(path) should contain theSameElementsAs vt.filter((p, _) => p != path)
      vt.remove(missingPath) should contain theSameElementsAs vt
      vt.remove(pathPrefix) should contain theSameElementsAs vt
    it should "allow to remove a path prefix, resulting in a pruned tree" in:
      vt.removePrefix(pathPrefix) should contain theSameElementsAs vt.filter((p, _) => !p.startsWith(pathPrefix))
      vt.removePrefix(Seq()).isEmpty shouldBe true
      vt.removePrefix(path) should contain theSameElementsAs vt.remove(path)
    it should "allow to update or add a value" in:
      val vtWithoutPath = vt.remove(path)
      val updated = vtWithoutPath.update(path, vt.get(path).get)
      updated.get(path) shouldBe vt.get(path)
      val updated2 = vtWithoutPath.update(path, transform(vt.get(path).get))
      updated2.get(path) shouldBe defined
      updated2.get(path).get shouldBe transform(vt.get(path).get)
    it should "allow concatenation" in:
      val vt2 = vt.mapValues((_, v) => transform(v))
      val concatenated = vt.remove(path).concat(vt2.remove(otherPath))
      concatenated.get(path) shouldBe defined
      concatenated.get(path).get shouldBe vt2.get(path).get
      concatenated.get(otherPath) shouldBe defined
      concatenated.get(otherPath).get shouldBe vt.get(otherPath).get
    it should "allow partitioning" in:
      val (partitioned1, partitioned2) = vt.partition((p, _) => p == path)
      partitioned1.get(path) shouldBe defined
      partitioned1.get(path).get shouldBe vt.get(path).get
      partitioned1.get(otherPath) shouldBe empty
      partitioned2.get(path) shouldBe empty
      partitioned2.get(otherPath) shouldBe defined
      partitioned2.get(otherPath).get shouldBe vt.get(otherPath).get
      partitioned2.size shouldBe vt.size - 1
    it should "allow to prepend a prefix to the entire tree" in:
      val prefixed = vt.prepend(path)
      prefixed.get(path) shouldBe empty
      prefixed.get(path ++ path) shouldBe defined
      prefixed.get(path ++ path).get shouldBe vt.get(path).get
    it should "allow to append a suffix to the entire tree" in:
      val suffixed = vt.append(path.reverse)
      suffixed.get(path) shouldBe empty
      suffixed.get(path ++ path.reverse) shouldBe defined
      suffixed.get(path ++ path.reverse).get shouldBe vt.get(path).get
    it should "be iterable" in:
      vt shouldBe a[Iterable[(Seq[N], V)]]
      vt should contain theSameElementsAs vt.toSeq
    it should "allow to reverse all paths" in:
      val reversed = vt.reversedNodes
      reversed.get(path.reverse) shouldBe defined
      reversed.get(path.reverse).get shouldBe vt.get(path).get
      reversed.get(path) shouldBe empty
    it should "provide an infix ++ concat operator" in:
      vt.removePrefix(Seq()) ++ vt should contain theSameElementsAs vt
    it should "provide an infix + update operator" in:
      vt.remove(path) + (path -> vt.get(path).get) should contain theSameElementsAs vt
    it should "implement partial function" in:
      vt.isDefinedAt(path) shouldBe true
      vt.isDefinedAt(missingPath) shouldBe false
      vt(path) shouldBe vt.get(path).get
  end nonEmptyValueTree
end ValueTreeTests
