package it.unibo.scafi.runtime

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.libraries.All.*
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.message.BinaryCodable
import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax

trait Programs:

  type ID = Int

  given BinaryCodable[ID] = compiletime.deferred

  type Lang = AggregateContext { type DeviceId = ID } & AggregateFoundation & FieldBasedSharedData & ExchangeSyntax &
    BranchingSyntax & FieldCalculusSyntax

  class ProgramWithResult[Result](val program: Lang ?=> Result, val expected: Map[ID, Result])

  given [Result]: CanEqual[Map[ID, Result], Map[ID, Result]] = CanEqual.derived

  def evolveProgram = ProgramWithResult(
    program = evolve(localId)(_ + 1),
    expected = Map(
      0 -> 1,
      1 -> 2,
      2 -> 3,
      3 -> 4,
    ),
  )

  def neighborsDiscoveryProgram = ProgramWithResult(
    program = neighborValues(localId).neighborValues,
    expected = Map(
      0 -> Map(1 -> 1, 2 -> 2),
      1 -> Map(0 -> 0, 3 -> 3),
      2 -> Map(0 -> 0, 3 -> 3),
      3 -> Map(1 -> 1, 2 -> 2),
    ),
  )

  def exchangeWithRestrictionsProgram = ProgramWithResult(
    program = branch(localId % 2 == 0)(
      exchange(100)(returnSending).neighborValues,
    )(
      exchange(200)(returnSending).neighborValues,
    ),
    expected = Map(
      0 -> Map(2 -> 100),
      1 -> Map(3 -> 200),
      2 -> Map(0 -> 100),
      3 -> Map(1 -> 200),
    ),
  )
end Programs
