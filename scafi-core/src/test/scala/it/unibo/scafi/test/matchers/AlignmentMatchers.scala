package it.unibo.scafi.test.matchers

import it.unibo.scafi.message.{ Export, ValueTree }

import org.scalatest.matchers.{ Matcher, MatchResult }

trait AlignmentMatchers:
  class AlignWith[DeviceId](val exportToTest: Export[DeviceId]) extends Matcher[Export[DeviceId]]:
    override def apply(left: Export[DeviceId]): MatchResult =
      given CanEqual[Set[DeviceId], Set[DeviceId]] = CanEqual.derived
      val result = left.devices == exportToTest.devices &&
        left.devices.forall(id => left(id).hasTheSamePathsAs(exportToTest(id)))
      MatchResult(
        result,
        s"Expected $left to be aligned with $exportToTest",
        s"Expected $left not to be aligned with $exportToTest",
      )

    extension (vt: ValueTree)
      def hasTheSamePathsAs(other: ValueTree): Boolean =
        vt.paths == other.paths

  def alignWith[DeviceId](exportToTest: Export[DeviceId]): AlignWith[DeviceId] = new AlignWith(exportToTest)

object AlignmentMatchers extends AlignmentMatchers
