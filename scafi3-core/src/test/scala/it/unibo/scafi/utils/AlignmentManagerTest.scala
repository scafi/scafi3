package it.unibo.scafi.utils

import it.unibo.scafi.message.Path

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should

class AlignmentManagerTest extends AnyFlatSpecLike, should.Matchers:
  private class Probe extends AlignmentManager:
    def path: Path = currentPath
    def scope[A](key: String)(body: => A): A = alignmentScope(key)(() => body)

  "AlignmentManager" should "restore the stack when an aligned body throws" in:
    val probe = Probe()

    assertThrows[RuntimeException]:
      probe.scope("outer"):
        probe.scope("inner"):
          throw RuntimeException("boom")

    probe.path shouldBe empty

    val nextPath = probe.scope("next")(probe.path)
    nextPath.map(_.key) shouldBe Seq("next")
end AlignmentManagerTest
