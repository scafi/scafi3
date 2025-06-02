package it.unibo.scafi.api

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.Inspectors
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.api.Api.Interface.{ *, given }
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory

class JVMApiTest extends AnyFlatSpec with should.Matchers with Inspectors:

  "Trivial program" should "work" in:
    type Lang = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & ExchangeCalculus

    def aggregateProgram(using l: Lang) =
      val res: l.SharedData[PortableDeviceId] =
        exchange(localId: l.SharedData[PortableDeviceId]): n =>
          (n, n)
      res.values

    val env = mooreGrid(3, 3, exchangeContextFactory)(aggregateProgram)
    env.cycleInOrder()
    env.cycleInReverseOrder()
    forAll(env.status): (id, field) =>
      forAll(field.toMap): (nid, nvalue) =>
        nvalue shouldBe (if nid <= id then nid else id)
