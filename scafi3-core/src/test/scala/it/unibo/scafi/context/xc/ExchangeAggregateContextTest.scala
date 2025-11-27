package it.unibo.scafi.context.xc

import it.unibo.scafi.UnitTest
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.common.syntax.BranchingSyntaxTest
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntaxTest
import it.unibo.scafi.language.xc.calculus.{ ExchangeCalculusSemanticsTestHelper, ExchangeCalculusSemanticsTests }
import it.unibo.scafi.laws.{ SharedDataApplicativeLaw, SharedDataMonoidLaw }
import it.unibo.scafi.libraries.All.{ exchange, localId, returning }
import it.unibo.scafi.message.ValueTree
import it.unibo.scafi.message.ValueTree.NoPathFoundException
import it.unibo.scafi.runtime.network.NetworkManager
import it.unibo.scafi.test.AggregateProgramProbe
import it.unibo.scafi.test.network.{ NeighborsNetworkManager, NoNeighborsNetworkManager }

import cats.syntax.all.toFunctorOps
import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class ExchangeAggregateContextTest
    extends AnyFlatSpecLike,
      should.Matchers,
      Inspectors,
      AggregateProgramProbe,
      BranchingSyntaxTest,
      FieldCalculusSyntaxTest,
      ExchangeCalculusSemanticsTests,
      SharedDataMonoidLaw,
      SharedDataApplicativeLaw,
      UnitTest:
  type Lang = ExchangeAggregateContext[Int]

  private val lang = exchangeContextFactory(NeighborsNetworkManager[Int](0, Set(1, 2, 4, 6)), ValueTree.empty)
  private given [A: Arbitrary] => Arbitrary[lang.SharedData[A]] = Arbitrary:
    given CanEqual[A, A] = CanEqual.derived
    for
      default <- Arbitrary.arbitrary[A]
      // Generate a random set (non empty) of device IDs between 0 and 10
      devices <- Gen.some(Gen.nonEmptyListOf(Gen.choose(0, 10))).map(_.get.toSet)
      neighbors <- Gen.mapOf:
        for
          id <- Gen.oneOf(devices)
          value <- Arbitrary.arbitrary[A]
        yield (id, value)
    yield lang.Field(default, devices, neighbors)

  private def exchangeContextFactoryHelper[Network <: NetworkManager { type DeviceId = Int }](
      network: Network,
      selfMessagesFromPreviousRound: ValueTree,
  ): ExchangeAggregateContext[Int] & ExchangeCalculusSemanticsTestHelper =
    new ExchangeAggregateContext(network.localId, network.receive, selfMessagesFromPreviousRound)
      with ExchangeCalculusSemanticsTestHelper:
      override def mockSharedData[T](default: T, devices: Set[DeviceId], values: Map[DeviceId, T]): SharedData[T] =
        Field(default, devices, values)

      override def unalignedDeviceId: DeviceId = -1

  "ExchangeContext" should behave like branchSpecification(exchangeContextFactory)
  it should behave like fieldCalculusSpecification(exchangeContextFactory)
  "Exchange" should behave like nvalues(exchangeContextFactoryHelper)
  "Field" should behave like sharedDataMonoidLaws("Field")(lang)
  it should behave like sharedDataApplicativeLaws("Field")(lang)

  "Exchange construct" should "return a different value than the one sent" in:
    def programRetSend(using Lang) = exchange(localId) { x => returning(x.map(_ + 1)) send x }.default
    val (result, exportValue) =
      roundForAggregateProgram(NoNeighborsNetworkManager(localId = 0), exchangeContextFactory)(programRetSend)
    result shouldBe 1
    val singlePath = exportValue(0).paths.head // There is only one path
    try exportValue(0).apply[Int](singlePath) shouldBe 0
    catch case _: NoPathFoundException => fail("The path should exist, but it was not found.")
end ExchangeAggregateContextTest
