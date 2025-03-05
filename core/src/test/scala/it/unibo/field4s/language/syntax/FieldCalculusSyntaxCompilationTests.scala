package it.unibo.field4s.language.syntax

import it.unibo.field4s.language.foundation.AggregateFoundationMock
import it.unibo.field4s.UnitTest
import it.unibo.field4s.language.foundation.AggregateFoundation

class FieldCalculusSyntaxCompilationTests extends UnitTest:

  val language: FieldCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with FieldCalculusSyntax:

    override def neighborValues[A](expr: A): AggregateValue[A] = mock[AggregateValue[A]]
    override def evolve[A](initial: A)(evolution: A => A): A = mock[A]
    override def share[A](initial: A)(shareAndReturning: AggregateValue[A] => A): A = mock[A]

  "FieldCalculus Syntax" should "compile" in:
    "val _: language.AggregateValue[Boolean] = language.nbr(true)" should compile
    "val _: language.AggregateValue[Int] = language.nbr(true)" shouldNot typeCheck

    "val _: Boolean = language.rep(true)(x => x)" should compile
    "val _: String = language.rep(true)(x => x)" shouldNot typeCheck

    "val _: Boolean = language.share(true)(x => x.onlySelf)" should compile
    "val _: String = language.share(true)(x => x.onlySelf)" shouldNot typeCheck
