package it.unibo.scafi.types

import scala.concurrent.{ ExecutionContext, Future }
import scala.scalajs.js
import scala.scalajs.js.timers.setTimeout

import it.unibo.scafi.test.AsyncSpec

class JSTypesTest extends AsyncSpec with JSTypes:

  override given executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  "JS outcomes" should "represent a Promise or a direct value" in:
    val outcome: Outcome[Int] = 42
    val future: Future[Int] = outcome
    val outcome2: Outcome[Int] = js.Promise[Int]: (resolve, _) =>
      setTimeout(100)(resolve(42): Unit)
    val future2: Future[Int] = outcome2
    for
      res1 <- future
      res2 <- future2
    yield
      res1 shouldBe 42
      res2 shouldBe 42

  "JS maps" should "be isomorphic to Scala maps" in:
    val scalaMap: collection.Map[Int, String] = Map(1 -> "one", 2 -> "two", 3 -> "three")
    val jsMap: js.Map[Int, String] = scalaMap
    val backToScala: collection.Map[Int, String] = jsMap
    backToScala shouldEqual scalaMap

  "JS functions" should "be isomorphic to Scala functions" in:
    val f0: js.Function0[Int] = () => 42
    val f1: js.Function1[Int, Int] = (x: Int) => x + 1
    val f2: js.Function2[Int, Int, Int] = (x: Int, y: Int) => x + y
    val scalaF0: () => Int = f0
    val scalaF1: Int => Int = f1
    val scalaF2: (Int, Int) => Int = f2
    scalaF0() shouldBe 42
    scalaF1(41) shouldBe 42
    scalaF2(40, 2) shouldBe 42

end JSTypesTest
