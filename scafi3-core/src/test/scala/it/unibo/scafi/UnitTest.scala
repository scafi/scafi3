package it.unibo.scafi

import scala.reflect.ClassTag

import org.scalatest.{ Inside, Inspectors, OptionValues }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers as ShouldMatchers
import org.scalatest.prop.TableDrivenPropertyChecks

trait UnitTest
    extends AnyFlatSpec
    with ShouldMatchers
    with OptionValues
    with Inside
    with Inspectors
    with TableDrivenPropertyChecks:

  export scala.language.postfixOps
  export org.scalatest.matchers.should.Matchers.*
  export math.Numeric.Implicits.infixNumericOps
  export org.scalatest.prop.{ TableFor1, TableFor2, TableFor3 }

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf", "DisableSyntax.null"))
  def mock[T]: T = null.asInstanceOf[T]

  extension [A](it: Iterable[A])
    def mean(using ops: Fractional[A]): A = ops.div(it.sum, ops.fromInt(it.size))

    def single: A =
      assert(it.tail.isEmpty, s">>> expected single element, but got: $it")
      assert(it.nonEmpty, s">>> expected single element, but got: $it")
      it.head

  extension (it: Any)

    def as[B: ClassTag]: B = it match
      case b: B => b
      case _ => fail(s"Could not cast $it to ${summon[ClassTag[B]].runtimeClass}")
end UnitTest
