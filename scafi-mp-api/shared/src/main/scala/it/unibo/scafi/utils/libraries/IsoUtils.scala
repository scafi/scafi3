package it.unibo.scafi.utils.libraries

import monocle.Iso

/**
 * A utility object making available, wherever an [[Iso]] instance is available, two implicit conversions allowing to
 * automatically convert back and forth without extra boilerplate code.
 */
object IsoUtils:

  given [A, B](using iso: Iso[A, B]): Conversion[A, B] with
    inline def apply(a: A): B = iso.get(a)

  given [A, B](using iso: Iso[A, B]): Conversion[B, A] with
    inline def apply(b: B): A = iso.reverseGet(b)
