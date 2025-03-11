package it.unibo.field4s.language.foundation

import it.unibo.field4s.language.AggregateFoundation

trait FieldMock:
  this: AggregateFoundation =>
  def mockField[T](items: Iterable[T]): SharedData[T]
