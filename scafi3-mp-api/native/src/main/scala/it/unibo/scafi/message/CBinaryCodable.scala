package it.unibo.scafi.message

import scala.scalanative.unsafe.Ptr

import it.unibo.scafi.nativebindings.structs.BinaryCodable as CBinaryCodable
import it.unibo.scafi.utils.CUtils.asVoidPtr

import cats.kernel.Hash

object CBinaryCodable:

  /** An instance of [[Hash]] for [[it.unibo.scafi.message.CBinaryCodable]]. */
  given Hash[Ptr[CBinaryCodable]] = new Hash[Ptr[CBinaryCodable]]:
    override def hash(x: Ptr[CBinaryCodable]): Int = (!x).hash(x).toInt
    override def eqv(x: Ptr[CBinaryCodable], y: Ptr[CBinaryCodable]): Boolean = (!x).cmp(x, y)
