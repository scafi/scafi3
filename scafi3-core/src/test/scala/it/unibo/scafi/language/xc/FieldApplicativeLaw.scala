package it.unibo.scafi.language.xc

import cats.Eq
import cats.laws.discipline.ApplicativeTests
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.ValueTree
import it.unibo.scafi.test.network.NoNeighborsNetworkManager
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class FieldApplicativeLaw extends AnyFunSuite, FunSuiteDiscipline, Checkers:
  private val lang: ExchangeAggregateContext[Int] =
    ExchangeAggregateContext.exchangeContextFactory(NoNeighborsNetworkManager(0), ValueTree.empty)
  private given [A] => Eq[lang.SharedData[A]] = Eq.fromUniversalEquals
  private given [A: Arbitrary] => Arbitrary[lang.SharedData[A]] = Arbitrary:
    for
      default <- Arbitrary.arbitrary[A]
      neighbors <- Gen.mapOf:
        for
          id <- Arbitrary.arbitrary[Int]
          value <- Arbitrary.arbitrary[A]
        yield (id, value)
    yield lang.Field(default, neighbors)
  checkAll("Field Applicative Laws", ApplicativeTests[lang.SharedData].apply[Int, Int, Int])
