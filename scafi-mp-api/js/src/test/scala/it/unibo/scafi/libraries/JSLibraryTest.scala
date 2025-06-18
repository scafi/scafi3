package it.unibo.scafi.libraries

import scala.util.Try

import it.unibo.scafi

import org.scalatest.matchers.should
import org.scalatest.Inspectors
import org.scalatest.wordspec.AnyWordSpec

import scafi.context.xc.ExchangeAggregateContext
import scafi.test.environment.Environment
import scafi.test.environment.Grids.mooreGrid
import scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import scafi.test.environment.Node

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait JSLibraryTest extends AnyWordSpec with should.Matchers with Inspectors:
  export ReturnSending.returnSending

  type ID = Int

  given Conversion[scalajs.js.Map[?, ?], Map[ID, ID]] = jsmap =>
    Try(jsmap.asInstanceOf[scalajs.js.Map[ID, ID]])
      .map(_.toMap)
      .getOrElse(fail(s"JavaScript runtime value $jsmap cannot be converted to Map[ID, ID]."))

  given Conversion[Any, ID] =
    case i: Int => i
    case x => fail(s"JavaScript runtime value $x cannot be converted to ID.")

  type Program[R] = (ExchangeAggregateContext[ID], Environment[R, ExchangeAggregateContext[ID]]) ?=> R

  inline def test[R](program: FullLibrary => R): (Environment[R, ExchangeAggregateContext[ID]], Map[Int, R]) =
    testIn[R](mooreGrid(3, 3, exchangeContextFactory))(program)

  def testIn[R](testEnvironment: Program[R] => Environment[R, ExchangeAggregateContext[ID]])(
      program: FullLibrary => R,
  ): (Environment[R, ExchangeAggregateContext[ID]], Map[ID, R]) =
    val env = testEnvironment(program(FullLibrary()))
    env.cycleInOrder()
    env.cycleInReverseOrder()
    (env, env.status)

  extension [R](env: Environment[R, ExchangeAggregateContext[ID]])
    def neighborsOf(id: ID): Set[Node[R, ExchangeAggregateContext[ID]]] =
      env.neighborsOf(env.nodes.find(_.id == id).getOrElse(fail(s"Node with id $id not found in the environment.")))
end JSLibraryTest
