package it.unibo.scafi.language.fc.syntax

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.libraries.All.{ evolve, localId, neighborValues }
import it.unibo.scafi.message.ValueTree
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.test.AggregateProgramProbe
import it.unibo.scafi.test.environment.Grids.mooreGrid
import it.unibo.scafi.test.environment.Node.inMemoryNetwork
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

trait FieldCalculusSyntaxTest extends AggregateProgramProbe:
  self: AnyFlatSpecLike & should.Matchers & Inspectors =>

  private type Language = AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax
  private type FieldCalculusContext =
    AggregateContext & AggregateFoundation { type DeviceId = Int } & FieldCalculusSyntax

  /**
   * Builds an [[AggregateContext]] where no neighbors are defined.
   * @return
   *   A [[NetworkManager]] with no neighbors.
   */
  private def noNeighborsNetwork(): NoNeighborsNetworkManager[Int] = NoNeighborsNetworkManager[Int]()

  /**
   * Common tests verifying the behavior of the [[FieldCalculusSyntax]].
   * @param contextFactory
   *   the factory used to build the [[Context]].
   * @tparam Context
   *   the [[FieldCalculusContext]] to be used.S
   */
  def fieldCalculusSpecification[Context <: FieldCalculusContext](
      contextFactory: (Int, NetworkManager { type DeviceId = Int }, ValueTree) => Context,
  ): Unit =
    "Neighboring operator" should "have an empty neighborhood when no devices are connected" in:
      def noNeighbors(using lang: Language): lang.SharedData[Int] =
        lang.neighborValues(localId)
      val id = 0
      val (result, _) = roundForAggregateProgram(id, noNeighborsNetwork(), contextFactory)(noNeighbors)
      result.size shouldBe 1
      result.headOption shouldBe Some(id)

    it should "produce a neighborhood with values produced by the neighbors" in:
      val env = mooreGrid(2, 2, contextFactory, inMemoryNetwork):
        neighborValues(localId).toList
      (0 until 2).foreach(_ => env.cycleInOrder())
      forAll(env.status): (id, neighborIds) =>
        neighborIds shouldBe (env.neighborsOf(id).get.map(_.id) + id).toList

    "Evolve operator" should "repeatedly apply a function to an initial value for every execution round" in:
      def evolveProgram(using lang: Language): Int = evolve(0)(_ + 1)
      val rounds = 10
      val engine = ScafiEngine(deviceId = 0, noNeighborsNetwork(), contextFactory)(evolveProgram)
      val results = (0 until rounds).map(_ => engine.cycle())
      results shouldBe (1 to rounds)
  end fieldCalculusSpecification
end FieldCalculusSyntaxTest
