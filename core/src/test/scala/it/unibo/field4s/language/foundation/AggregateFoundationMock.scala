package it.unibo.field4s.language.foundation

import it.unibo.field4s.abstractions.{ Aggregate, Liftable }
import it.unibo.field4s.collections.SafeIterable

class AggregateFoundationMock
    extends AggregateFoundation
    with DeviceAwareAggregateFoundation
    with FieldMock
    with DeviceMock:
  override type AggregateValue[T] = MockAggregate[T]

  case class MockAggregate[T](mockedValues: Iterable[T] = Seq()) extends SafeIterable[T]:
    override protected def iterator: Iterator[T] = mockedValues.iterator

  override given aggregate: Aggregate[MockAggregate] = new Aggregate[MockAggregate]:

    extension [A](a: MockAggregate[A])
      override def withoutSelf: SafeIterable[A] = MockAggregate(a.mockedValues.tail)
      override def onlySelf: A = a.mockedValues.head

  override given liftable: Liftable[MockAggregate] = new Liftable[MockAggregate]:
    override def lift[A, B](a: MockAggregate[A])(f: A => B): MockAggregate[B] = MockAggregate(a.mockedValues.map(f))

    override def lift[A, B, C](a: MockAggregate[A], b: MockAggregate[B])(f: (A, B) => C): MockAggregate[C] =
      MockAggregate(a.mockedValues.zip(b.mockedValues).map(f.tupled))

    override def lift[A, B, C, D](a: MockAggregate[A], b: MockAggregate[B], c: MockAggregate[C])(
        f: (A, B, C) => D,
    ): MockAggregate[D] = MockAggregate(
      a.mockedValues.zip(b.mockedValues).zip(c.mockedValues).map(t => (t._1._1, t._1._2, t._2)).map(f.tupled),
    )

  override def mockField[T](items: Iterable[T]): MockAggregate[T] = MockAggregate(items)
end AggregateFoundationMock
