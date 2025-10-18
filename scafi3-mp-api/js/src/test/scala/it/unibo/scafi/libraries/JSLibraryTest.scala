// package it.unibo.scafi.libraries

// import scala.util.Try

// import it.unibo.scafi

// import org.scalatest.Inspectors
// import org.scalatest.matchers.should
// import org.scalatest.wordspec.AnyWordSpec

// import scafi.context.xc.ExchangeAggregateContext
// import scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
// import scafi.runtime.network.NetworkManager
// import scafi.test.environment.Environment
// import scafi.test.environment.Grids.mooreGrid
// import scafi.test.environment.Node.inMemoryNetwork

// @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
// trait JSLibraryTest extends AnyWordSpec with should.Matchers with Inspectors:
//   export ReturnSending.returnSending

//   type ID = Int

//   given Conversion[scalajs.js.Map[?, ?], Map[ID, ID]] = jsmap =>
//     Try(jsmap.asInstanceOf[scalajs.js.Map[ID, ID]])
//       .map(_.toMap)
//       .getOrElse(fail(s"JavaScript runtime value $jsmap cannot be converted to Map[ID, ID]."))

//   given Conversion[Any, ID] =
//     case i: Int => i
//     case x => fail(s"JavaScript runtime value $x cannot be converted to ID.")

//   type Program[Result] = (
//       ExchangeAggregateContext[ID],
//       Environment[Result, ExchangeAggregateContext[ID], NetworkManager { type DeviceId = ID }],
//   ) ?=> Result

//   inline def test[Result](
//       program: FullLibrary => Result,
//   ): (Environment[Result, ExchangeAggregateContext[ID], NetworkManager { type DeviceId = ID }], Map[Int, Result]) =
//     testIn[Result](mooreGrid(sizeX = 3, sizeY = 3, exchangeContextFactory, inMemoryNetwork))(program)

//   def testIn[Result](
//       env: Program[Result] => Environment[Result, ExchangeAggregateContext[ID], NetworkManager { type DeviceId = ID }],
//   )(
//       program: FullLibrary => Result,
//   ): (Environment[Result, ExchangeAggregateContext[ID], NetworkManager { type DeviceId = ID }], Map[ID, Result]) =
//     val environment = env(program(FullLibrary()))
//     environment.cycleInOrder()
//     environment.cycleInReverseOrder()
//     (environment, environment.status)
// end JSLibraryTest
