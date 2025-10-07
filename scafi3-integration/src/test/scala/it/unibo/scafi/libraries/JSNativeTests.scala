package it.unibo.scafi.libraries

import java.util.concurrent.Executors

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.Future.sequence

import it.unibo.scafi.integration.JSPlatformTest
import it.unibo.scafi.integration.PlatformTest.ProgramOutput
import it.unibo.scafi.test.environment.Grids.vonNeumannGrid
import it.unibo.scafi.utils.FreePortFinder

import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.{ Seconds, Span }

class JSNativeTests extends AnyFlatSpec with JSPlatformTest with ScalaFutures:

  given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  given PatienceConfig = PatienceConfig(timeout = Span(180, Seconds))

  "Neighbors discovery program" should "spread local values to neighborhood" in:
    sequence:
      jsAggregateResult("neighbors-discovery", rows = 2, cols = 2)
    .futureValue should contain theSameElementsAs Seq(
      0 -> jsField(default = 0, neighbors = Map(0 -> 0, 1 -> 1, 2 -> 2)),
      1 -> jsField(default = 1, neighbors = Map(0 -> 0, 1 -> 1, 3 -> 3)),
      2 -> jsField(default = 2, neighbors = Map(0 -> 0, 2 -> 2, 3 -> 3)),
      3 -> jsField(default = 3, neighbors = Map(1 -> 1, 2 -> 2, 3 -> 3)),
    )

  "Exchange aggregate program with branch restriction" should "correctly spread local values to aligned neighbors" in:
    sequence:
      jsAggregateResult("restricted-exchange", rows = 2, cols = 2)
    .futureValue should contain theSameElementsAs Seq(
      0 -> jsField(default = true, neighbors = Map(0 -> true, 2 -> true)),
      1 -> jsField(default = false, neighbors = Map(1 -> false, 3 -> false)),
      2 -> jsField(default = true, neighbors = Map(0 -> true, 2 -> true)),
      3 -> jsField(default = false, neighbors = Map(1 -> false, 3 -> false)),
    )

  "Protobuf exchange aggregate program" should "correctly exchange protobuf messages" in sensorTestWith("protobuf")

  "JSON exchange aggregate program" should "correctly exchange JSON messages" in sensorTestWith("json")

  inline def sensorTestWith(format: String): Assertion =
    def jsSensor(id: Int): String = s"Sensor(id=#$id, temp=${id * 10})"
    sequence:
      jsAggregateResult(s"${format}-exchange", rows = 2, cols = 2)
    .futureValue should contain theSameElementsAs Seq(
      0 -> jsField(default = jsSensor(0), neighbors = Map(0 -> jsSensor(0), 1 -> jsSensor(1), 2 -> jsSensor(2))),
      1 -> jsField(default = jsSensor(1), neighbors = Map(0 -> jsSensor(0), 1 -> jsSensor(1), 3 -> jsSensor(3))),
      2 -> jsField(default = jsSensor(2), neighbors = Map(0 -> jsSensor(0), 2 -> jsSensor(2), 3 -> jsSensor(3))),
      3 -> jsField(default = jsSensor(3), neighbors = Map(1 -> jsSensor(1), 2 -> jsSensor(2), 3 -> jsSensor(3))),
    )

  private def jsAggregateResult(testName: String, rows: Int, cols: Int): Seq[Future[(Int, ProgramOutput)]] =
    val ports = FreePortFinder.get(rows * cols)
    vonNeumannGrid(rows, cols): (id, neighbors) =>
      for
        neighborsAsJsEntries = neighbors
          .map(nid => s"[$nid, Runtime.Endpoint('localhost', ${ports(nid)})]")
          .mkString("[", ", ", "]")
        res <- Future:
          testProgram(testName):
            "{{ deviceId }}" -> id.toString
            "{{ port }}" -> ports(id).toString
            "{{ neighbors }}" -> neighborsAsJsEntries
      yield id -> res.getOrElse(fail(s"JS test '$testName' failed on device '$id': ${res.failed.get.getMessage}"))

  private def jsField[V <: Any](default: V, neighbors: Map[Int, V]): String = s"Field($default, $neighbors)"
end JSNativeTests
