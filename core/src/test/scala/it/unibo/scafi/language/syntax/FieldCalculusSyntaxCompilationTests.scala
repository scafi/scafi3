package it.unibo.scafi.language.syntax

import it.unibo.scafi.language.foundation.AggregateFoundationMock
import it.unibo.scafi.UnitTest
import it.unibo.scafi.language.AggregateFoundation

class FieldCalculusSyntaxCompilationTests extends UnitTest:

  val language: FieldCalculusSyntax & AggregateFoundation = new AggregateFoundationMock with FieldCalculusSyntax:

    override def neighborValues[A](expr: A): SharedData[A] = mock[SharedData[A]]
    override def evolve[A](initial: A)(evolution: A => A): A = mock[A]
    override def share[A](initial: A)(shareAndReturning: SharedData[A] => A): A = mock[A]

  "FieldCalculus Syntax" should "compile" in:
    "val _: language.SharedData[Boolean] = language.neighborValues(true)" should compile
    "val _: language.SharedData[Int] = language.neighborValues(true)" shouldNot typeCheck

    "val _: Boolean = language.evolve(true)(x => x)" should compile
    "val _: String = language.evolve(true)(x => x)" shouldNot typeCheck

    "val _: Boolean = language.share(true)(x => x.onlySelf)" should compile
    "val _: String = language.share(true)(x => x.onlySelf)" shouldNot typeCheck
