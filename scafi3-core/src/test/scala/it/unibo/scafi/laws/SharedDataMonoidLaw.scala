package it.unibo.scafi.laws

import it.unibo.scafi.context.xc.ExchangeAggregateContext

import cats.Eq
import cats.kernel.Monoid
import cats.kernel.laws.discipline.MonoidTests
import org.scalacheck.Arbitrary
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FlatSpecDiscipline

trait SharedDataMonoidLaw extends FlatSpecDiscipline, Inspectors, Checkers:
  self: AnyFlatSpecLike =>

  def sharedDataMonoidLaws[C: Monoid](sharedDataName: String)(
      lang: ExchangeAggregateContext[C],
  )(using Arbitrary[lang.SharedData[C]]): Unit =
    given [A] => Eq[lang.SharedData[A]] = Eq.fromUniversalEquals
    checkAll(s"$sharedDataName Monoid Laws", MonoidTests[lang.SharedData[C]].monoid)
