package it.unibo.scafi.api

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.api.JSXCApi.JSInterface.{ *, given }
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.Inspectors

class JSXCApiTest extends AnyFlatSpec with should.Matchers with Inspectors:

  "Trivial program" should "work" in:
    type Language = AggregateFoundation { type DeviceId = Int } & ExchangeSyntax & FieldBasedSharedData

    def aggregateProgram(using Language) =
      exchange(PortableField(localId, scalajs.js.Map.empty)): n =>
        (n, n)

    val env = mooreGrid(3, 3, exchangeContextFactory)(aggregateProgram)
    env.cycleInOrder()
    env.cycleInReverseOrder()
    forAll(env.status): (id, field) =>
      forAll(field.neighborValues.toSeq): (nid, nvalue) =>
        nvalue shouldBe (if nid <= id then nid else id)
