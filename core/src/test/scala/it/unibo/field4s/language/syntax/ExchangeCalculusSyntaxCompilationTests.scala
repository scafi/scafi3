package it.unibo.field4s.language.syntax

import it.unibo.field4s.language.foundation.AggregateFoundationMock
import it.unibo.field4s.UnitTest
import it.unibo.field4s.language.foundation.AggregateFoundation
import it.unibo.field4s.language.syntax.common.ReturnSending
import it.unibo.field4s.language.syntax.common.ReturnSending.*

class ExchangeCalculusSyntaxCompilationTests extends UnitTest:

  val language: ExchangeCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with ExchangeCalculusSyntax:

    override def exchange[T](initial: SharedData[T])(
        f: SharedData[T] => ReturnSending[SharedData[T]],
    ): SharedData[T] = mock[SharedData[T]]

  "ExchangeCalculus Syntax" should "compile" in:
    val field: language.SharedData[Boolean] = mock[language.SharedData[Boolean]]
    val intField = mock[language.SharedData[Int]]
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => x)" should compile
    "val _: language.SharedData[Int] = language.exchange(intField)(x => ret (x) send x)" should compile
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => (x, x))" should compile
    "val _: language.SharedData[Int] = language.exchange(field)(x => x)" shouldNot typeCheck
