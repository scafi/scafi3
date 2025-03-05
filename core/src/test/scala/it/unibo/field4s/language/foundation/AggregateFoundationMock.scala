package it.unibo.field4s.language.foundation

import it.unibo.field4s.abstractions.SharedDataOps
import it.unibo.field4s.collections.SafeIterable

import cats.Applicative
import cats.syntax.all.*

class AggregateFoundationMock
    extends AggregateFoundation
    with DeviceAwareAggregateFoundation
    with FieldMock
    with DeviceMock:
  override type SharedData[T] = MockAggregate[T]

  case class MockAggregate[T](mockedValues: Iterable[T] = Seq()) extends SafeIterable[T]:
    override protected def iterator: Iterator[T] = mockedValues.iterator

  override given aggregate: SharedDataOps[MockAggregate] = new SharedDataOps[MockAggregate]:
    extension [A](a: MockAggregate[A])
      override def withoutSelf: SafeIterable[A] = MockAggregate(a.mockedValues.tail)
      override def onlySelf: A = a.mockedValues.head

  override given liftable: Applicative[MockAggregate] = new Applicative[MockAggregate]:
    override def pure[A](x: A): MockAggregate[A] = MockAggregate(Seq(x))

    override def ap[A, B](ff: MockAggregate[A => B])(fa: MockAggregate[A]): MockAggregate[B] =
      MockAggregate(ff.mockedValues.zip(fa.mockedValues).map { case (f, a) => f(a) })

    override def map[A, B](fa: MockAggregate[A])(f: A => B): MockAggregate[B] =
      MockAggregate(fa.mockedValues.map(f))

  override def mockField[T](items: Iterable[T]): MockAggregate[T] = MockAggregate(items)
end AggregateFoundationMock
