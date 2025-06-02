package it.unibo.scafi.utils.api

import monocle.Iso

object IsoUtils:

  given [A, B](using iso: Iso[A, B]): Conversion[A, B] with
    inline def apply(a: A): B = iso.get(a)

  given [A, B](using iso: Iso[A, B]): Conversion[B, A] with
    inline def apply(b: B): A = iso.reverseGet(b)
