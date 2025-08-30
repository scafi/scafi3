package it.unibo.scafi.language.foundation

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.abstractions.AggregateTests
import it.unibo.scafi.UnitTest

import cats.syntax.all.{ catsSyntaxTuple2Semigroupal, toFunctorOps }

trait AggregateFoundationTests:
  this: UnitTest & AggregateTests =>

  type A <: AggregateFoundation & FieldMock
  val lang: A

  def aggregateFoundation(): Unit =
    val field: lang.SharedData[String] = lang.mockField(List("a", "b", "c"))
    it should "provide foldable fields" in:
      field.fold("")(_ + _) should be("abc")
    it should "provide fields that have a local value" in:
      field.onlySelf should be("a")
    it should "provide fields that have a neighbouring foldable value" in:
      field.withoutSelf.fold("")(_ + _) should be("bc")
    it should "provide a way to map fields" in:
      field.map(_.length).fold(0)(_ + _) should be(3)
    it should "provide a way to combine fields" in:
      (field, field).mapN(_ + _).fold("")(_ + _) should be("aabbcc")
//    val _: lang.SharedData[Int] = lang.mockField(List(1, 2, 3))
//    val _: lang.SharedData[Int] = lang.mockField(List(4, 5, 6))
//    val _: lang.SharedData[Int] = lang.mockField(List(7, 8, 9))
    it should behave like aggregate(field)
  end aggregateFoundation
end AggregateFoundationTests
