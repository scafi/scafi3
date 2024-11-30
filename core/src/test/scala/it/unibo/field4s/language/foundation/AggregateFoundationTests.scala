package it.unibo.field4s.language.foundation

import it.unibo.field4s.abstractions.*
import it.unibo.field4s.abstractions.Liftable.*
import it.unibo.field4s.UnitTest

trait AggregateFoundationTests:
  this: UnitTest & AggregateTests & LiftableTests =>

  type A <: AggregateFoundation & FieldMock
  val lang: A

  override type F[T] = lang.AggregateValue[T]
  override def mappable: Liftable[lang.AggregateValue] = lang.liftable

  def aggregateFoundation(): Unit =
    val field: lang.AggregateValue[String] = lang.mockField(List("a", "b", "c"))
    it should "provide foldable fields" in:
      field.fold("")(_ + _) should be("abc")
    it should "provide fields that have a local value" in:
      field.onlySelf should be("a")
    it should "provide fields that have a neighbouring foldable value" in:
      field.withoutSelf.fold("")(_ + _) should be("bc")
    it should "provide a way to map fields" in:
      field.map(_.length).fold(0)(_ + _) should be(3)
    it should "provide a way to combine fields" in:
      lift(field, field)(_ + _).fold("")(_ + _) should be("aabbcc")
    val intField: lang.AggregateValue[Int] = lang.mockField(List(1, 2, 3))
    val intField2: lang.AggregateValue[Int] = lang.mockField(List(4, 5, 6))
    val intField3: lang.AggregateValue[Int] = lang.mockField(List(7, 8, 9))
    val intFieldOfFields: lang.AggregateValue[lang.AggregateValue[Int]] =
      lang.mockField(List(intField, intField2, intField3))
    "AggregateValue" should behave like liftable(intField, intFieldOfFields)
    it should behave like aggregate(field)
  end aggregateFoundation
end AggregateFoundationTests
