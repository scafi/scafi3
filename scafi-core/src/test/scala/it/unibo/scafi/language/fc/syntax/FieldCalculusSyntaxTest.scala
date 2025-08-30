package it.unibo.scafi.language.fc.syntax

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.libraries.All.localId
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.test.AggregateProgramProbe
import it.unibo.scafi.test.network.NoNeighborsNetworkManager

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

trait FieldCalculusSyntaxTest extends AggregateProgramProbe:
  self: AnyFlatSpecLike & should.Matchers =>

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
      contextFactory: (Int, NetworkManager { type DeviceId = Int }) => Context,
  ): Unit =
    "Neighboring operator" should "have an empty neighborhood when no devices are connected" in:
      def noNeighbors(using lang: Language): lang.SharedData[Int] =
        lang.neighborValues(localId)
      val id = 0
      val (result, _) = roundForAggregateProgram(id, noNeighborsNetwork(), contextFactory)(noNeighbors)
      result.size shouldBe 1
      result.headOption shouldBe Some(id)

    it should "produce a neighborhood with values produced by the neighbors" in:
//      def neighborhoodValues(using lang: Language): lang.SharedData[Int] =
//        lang.neighborValues(localId)
      ()
end FieldCalculusSyntaxTest
