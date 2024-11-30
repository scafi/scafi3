package it.unibo.field4s.language.foundation

trait FieldMock:
  this: AggregateFoundation =>
  def mockField[T](items: Iterable[T]): AggregateValue[T]
