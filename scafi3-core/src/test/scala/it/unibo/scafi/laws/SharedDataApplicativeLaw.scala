package it.unibo.scafi.laws

import it.unibo.scafi.context.xc.ExchangeAggregateContext

import cats.Eq
import cats.laws.discipline.ApplicativeTests
import org.scalacheck.{ Arbitrary, Cogen }
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FlatSpecDiscipline

trait SharedDataApplicativeLaw extends FlatSpecDiscipline, Inspectors, Checkers:
  self: AnyFlatSpecLike =>

  def sharedDataApplicativeLaws[C](sharedDataName: String)(
      lang: ExchangeAggregateContext[C],
  )(using Arbitrary[C], Arbitrary[lang.SharedData[C]], Arbitrary[lang.SharedData[C => C]], Cogen[C]): Unit =
    given [A] => Eq[lang.SharedData[A]] = Eq.fromUniversalEquals
    checkAll(s"$sharedDataName Applicative Laws", ApplicativeTests[lang.SharedData].apply[C, C, C])
