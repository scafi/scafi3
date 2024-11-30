package it.unibo.field4s.language.syntax

import it.unibo.field4s.language.foundation.AggregateFoundationMock
import it.unibo.field4s.UnitTest
import it.unibo.field4s.language.foundation.AggregateFoundation
import it.unibo.field4s.language.syntax.common.RetSend
import it.unibo.field4s.language.syntax.common.RetSend.*

class ExchangeCalculusSyntaxCompilationTests extends UnitTest:

  val language: ExchangeCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with ExchangeCalculusSyntax:

    override def exchange[T](initial: AggregateValue[T])(
        f: AggregateValue[T] => RetSend[AggregateValue[T]],
    ): AggregateValue[T] = mock[AggregateValue[T]]

  "ExchangeCalculus Syntax" should "compile" in:
    val field: language.AggregateValue[Boolean] = mock[language.AggregateValue[Boolean]]
    val intField = mock[language.AggregateValue[Int]]
    "val _: language.AggregateValue[Boolean] = language.exchange(field)(x => x)" should compile
    "val _: language.AggregateValue[Int] = language.exchange(intField)(x => ret (x) send x)" should compile
    "val _: language.AggregateValue[Boolean] = language.exchange(field)(x => (x, x))" should compile
    "val _: language.AggregateValue[Int] = language.exchange(field)(x => x)" shouldNot typeCheck
