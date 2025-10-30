package it.unibo.scafi.libraries

import java.util.concurrent.Executors

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.Future.sequence

import it.unibo.scafi.integration.PlatformTest
import it.unibo.scafi.integration.PlatformTest.ProgramOutput
import it.unibo.scafi.runtime.network.sockets.InetTypes.Port
import it.unibo.scafi.test.environment.Grids.vonNeumannGrid
import it.unibo.scafi.utils.FreePortFinder

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.time.{ Seconds, Span }

trait CrossLanguageTests extends AnyFlatSpec with PlatformTest with ScalaFutures with should.Matchers:

  given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  given PatienceConfig = PatienceConfig(timeout = Span(180, Seconds))

  type ID = Int

  def neighborsDiscoveryTest(): Unit =
    "Neighbors discovery program" should "spread local values to neighborhood" in:
      sequence:
        aggregateResult("neighbors-discovery", rows = 2, cols = 2)
      .futureValue should contain theSameElementsAs Seq(
        0 -> fieldRepr(default = 0, neighbors = Map(0 -> 0, 1 -> 1, 2 -> 2)),
        1 -> fieldRepr(default = 1, neighbors = Map(0 -> 0, 1 -> 1, 3 -> 3)),
        2 -> fieldRepr(default = 2, neighbors = Map(0 -> 0, 2 -> 2, 3 -> 3)),
        3 -> fieldRepr(default = 3, neighbors = Map(1 -> 1, 2 -> 2, 3 -> 3)),
      )

  def domainRestrictionTest(): Unit =
    "Exchange program with branch restriction" should "correctly spread local values to aligned neighbors" in:
      sequence:
        aggregateResult("restricted-exchange", rows = 2, cols = 2)
      .futureValue should contain theSameElementsAs Seq(
        0 -> fieldRepr(default = 1, neighbors = Map(0 -> 1, 2 -> 1)),
        1 -> fieldRepr(default = 0, neighbors = Map(1 -> 0, 3 -> 0)),
        2 -> fieldRepr(default = 1, neighbors = Map(0 -> 1, 2 -> 1)),
        3 -> fieldRepr(default = 0, neighbors = Map(1 -> 0, 3 -> 0)),
      )

  def sensorExchangeTestWith(format: String): Unit =
    def sensor(id: Int): String = s"Sensor{id=#$id, temp=${id * 10}.00}"
    "Exchange aggregate program" should s"correctly exchange $format messages" in:
      sequence:
        aggregateResult(s"$format-exchange", rows = 2, cols = 2)
      .futureValue should contain theSameElementsAs Seq(
        0 -> fieldRepr(default = sensor(0), neighbors = Map(0 -> sensor(0), 1 -> sensor(1), 2 -> sensor(2))),
        1 -> fieldRepr(default = sensor(1), neighbors = Map(0 -> sensor(0), 1 -> sensor(1), 3 -> sensor(3))),
        2 -> fieldRepr(default = sensor(2), neighbors = Map(0 -> sensor(0), 2 -> sensor(2), 3 -> sensor(3))),
        3 -> fieldRepr(default = sensor(3), neighbors = Map(1 -> sensor(1), 2 -> sensor(2), 3 -> sensor(3))),
      )

  protected def aggregateResult(testName: String, rows: Int, cols: Int): Seq[Future[(Int, ProgramOutput)]] =
    val ports = FreePortFinder.get(rows * cols)
    vonNeumannGrid(rows, cols): (id, neighbors) =>
      Future:
        testProgram(testName):
          "{{ deviceId }}" -> id.toString
          "{{ port }}" -> ports(id).toString
          "{{ neighbors }}" -> neighborsAsCode(id, neighbors, ports)
      .map(res => id -> res.getOrElse(fail(s"Test '$testName' failed on device '$id': ${res.failed.get.getMessage}")))

  /**
   * Builds the code in the targeted language to inject into the test template to specify neighboring relationships. * @param
   * @param id
   *   The identifier of the current device.
   * @param neighbors
   *   The set of identifiers of the neighboring devices.
   * @param ports
   *   The sequence of ports to assign to each device.
   */
  def neighborsAsCode(id: ID, neighbors: Set[ID], ports: Seq[Port]): String

  /** The representation of a field in the targeted language, given a default value and a map of neighbor values. */
  def fieldRepr[Value](default: Value, neighbors: Map[Int, Value]): String
end CrossLanguageTests
