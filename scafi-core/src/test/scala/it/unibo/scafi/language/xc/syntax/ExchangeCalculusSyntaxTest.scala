package it.unibo.scafi.language.xc.syntax

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.language.xc.syntax.ReturnSending.*
import it.unibo.scafi.language.xc.syntax.{ ExchangeCalculusSyntax, ReturnSending }
import it.unibo.scafi.utils.ScafiTestUtils

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ExchangeCalculusSyntaxTest extends AnyFlatSpecLike, should.Matchers, ScafiTestUtils:
  val language: ExchangeCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with ExchangeCalculusSyntax:
    override def exchange[T](initial: SharedData[T])(
        f: SharedData[T] => ReturnSending[SharedData[T]],
    ): SharedData[T] = MockAggregate()

  "ExchangeCalculus Syntax" should "compile" in:
    val field: language.SharedData[Boolean] = placeholder
    val intField: language.SharedData[Int] = placeholder
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => x)" should compile
    "val _: language.SharedData[Int] = language.exchange(intField)(x => returning (x) send x)" should compile
    "val _: language.SharedData[Boolean] = language.exchange(field)(x => (x, x))" should compile
    "val _: language.SharedData[Int] = language.exchange(field)(x => x)" shouldNot typeCheck
