package it.unibo.scafi.message

import scala.scalanative.unsafe.Ptr

import it.unibo.scafi.nativebindings.structs.{ BinaryCodable as CBinaryCodable, Eq as CEq }
import it.unibo.scafi.utils.CUtils.asVoidPtr

import cats.kernel.Hash

object CEq:
  export cats.implicits.catsSyntaxEq

  given Hash[Ptr[CEq]] = new Hash[Ptr[CEq]]:
    override def hash(x: Ptr[CEq]): Int = (!x).hash(x).toInt
    override def eqv(x: Ptr[CEq], y: Ptr[CEq]): Boolean = (!x).cmp(x, y)

object CBinaryCodable:
  export cats.implicits.catsSyntaxEq

  /** An instance of [[Hash]] for [[it.unibo.scafi.message.CBinaryCodable]]. */
  given Hash[Ptr[CBinaryCodable]] = new Hash[Ptr[CBinaryCodable]]:
    override def hash(x: Ptr[CBinaryCodable]): Int = (!x)._eq.hash(x).toInt
    override def eqv(x: Ptr[CBinaryCodable], y: Ptr[CBinaryCodable]): Boolean = (!x)._eq.cmp(x, y)
