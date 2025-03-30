package it.unibo.scafi.language.syntax

import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.syntax.common.ReturnSending
import it.unibo.scafi.language.syntax.common.ReturnSending.*

class ExchangeCalculusSyntaxCompilationTests extends UnitTest:

  val language: ExchangeCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with ExchangeCalculusSyntax:

    override def exchange[T](initial: SharedData[T])(
        f: SharedData[T] => ReturnSending[SharedData[T]],
    ): SharedData[T] = mock[SharedData[T]]

  "ExchangeCalculus Syntax" should "compile" in:
    val field: language.SharedData[Boolean] = mock[language.SharedData[Boolean]]
    val intField = mock[language.SharedData[Int]]
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => x)" should compile
    "val _: language.SharedData[Int] = language.exchange(intField)(x => returning (x) send x)" should compile
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => (x, x))" should compile
    "val _: language.SharedData[Int] = language.exchange(field)(x => x)" shouldNot typeCheck
