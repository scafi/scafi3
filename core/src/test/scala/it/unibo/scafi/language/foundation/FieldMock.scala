package it.unibo.scafi.language.foundation

import it.unibo.scafi.language.AggregateFoundation

trait FieldMock:
  this: AggregateFoundation =>
  def mockField[T](items: Iterable[T]): SharedData[T]
