package it.unibo.scafi.types

import scala.scalanative.unsafe.{ alloc, CBool, CInt, CStruct2, Ptr, Zone }
import scala.scalanative.unsigned.toUInt
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.nativebindings.structs.Eq as CEq

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
class NativeTypesTest extends AnyWordSpec with should.Matchers with NativeTypes:

  "C arrays" when:
    "created from Scala" should:
      "be possible to convert them back and forth" in:
        Zone:
          val scalaSeq = (1 to 10).map(x => alloc[Int]().tap(ptr => !ptr = x))
          val cArray: Seq[Ptr[CInt]] = scalaSeq
          val backToScala: collection.Seq[Ptr[CInt]] = cArray
          backToScala.map(!_) shouldEqual (1 to 10)

  "C maps" when:
    "created from Scala" should:
      "be possible to convert them back and forth" in:
        Zone:
          val scalaMap = (1 to 10)
            .map(x => alloc[CInt]().tap(!_ = x) -> alloc[CBool]().tap(!_ = (x % 2 == 0)))
            .toMap
          val cMap: Map[Ptr[CInt], Ptr[CBool]] = scalaMap
          val backToScala: collection.Map[Ptr[CInt], Ptr[CBool]] = cMap
          backToScala shouldEqual scalaMap
          CMap.free(cMap)

    "created and manipulated from C-side" should:
      "contain all expected elements" in:
        Zone:
          val cMap = CMap.empty
          val entries = (1 to 5).map: x =>
            val key = EqInt(x)
            val value = alloc[CInt]().tap(!_ = x * 10)
            (key, value)
          entries.foreach: (key, value) =>
            val oldValue = CMap.put(cMap, key, value)
            oldValue shouldBe null
          CMap.size(cMap).toInt shouldBe 5
          entries.foreach: (key, value) =>
            val got = CMap.get(cMap, key)
            got.asInstanceOf[Ptr[CInt]] shouldEqual value
          CMap.free(cMap)

      "allow entries to be updated based on the key" in:
        Zone:
          val cMap = CMap.empty
          val oldValue = CMap.put(cMap, EqInt(1), alloc[CInt]().tap(!_ = 10))
          oldValue shouldBe null
          val oldValue2 = CMap.put(cMap, EqInt(1), alloc[CInt]().tap(!_ = 20))
          !oldValue2.asInstanceOf[Ptr[CInt]] shouldBe 10
          val got = CMap.get(cMap, EqInt(1))
          !got.asInstanceOf[Ptr[CInt]] shouldBe 20
          CMap.free(cMap)

  "C function pointers" should:
    "be isomorphic to Scala lambdas" in:
      Zone:
        val cFuncPtr0: Function0[Int] = () => 42
        val backToScala0: () => Int = cFuncPtr0
        backToScala0() shouldBe 42
        val cFuncPtr: Function1[Int, Int] = (x: Int) => x + 1
        val backToScala: Int => Int = cFuncPtr
        backToScala(41) shouldBe 42
        val cFuncPtr2: Function2[Int, Int, Int] = (x: Int, y: Int) => x + y
        val backToScala2: (Int, Int) => Int = cFuncPtr2
        backToScala2(40, 2) shouldBe 42

  type EqInt = CStruct2[CEq, CInt]

  object EqInt:
    def apply(x: Int)(using Zone): Ptr[EqInt] =
      val cEq = CEq()
      (!cEq).cmp = (a: Ptr[Byte], b: Ptr[Byte]) => a.asInstanceOf[Ptr[EqInt]]._2 == b.asInstanceOf[Ptr[EqInt]]._2
      (!cEq).hash = (a: Ptr[Byte]) => a.asInstanceOf[Ptr[EqInt]]._2.toInt.hashCode().toUInt
      alloc[EqInt]().tap: ptr =>
        ptr._1 = !cEq
        ptr._2 = x

  // in C a Ptr[EqInt] can be used wherever a Ptr[CEq] is expected since EqInt is a struct starting with a CEq
  given Conversion[Ptr[EqInt], Ptr[CEq]] = ptr => ptr.asInstanceOf[Ptr[CEq]]

end NativeTypesTest
