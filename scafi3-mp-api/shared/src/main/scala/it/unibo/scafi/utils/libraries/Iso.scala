package it.unibo.scafi.utils.libraries

/**
 * An isomorphism between two types `A` and `B`.
 */
trait Iso[A, B]:

  /**
   * Converts an instance of type `A` to an instance of type `B`.
   * @param a
   *   the instance to convert
   * @return
   *   the converted instance
   */
  def to(a: A): B

  /**
   * Converts an instance of type `B` to an instance of type `A`.
   * @param b
   *   the instance to convert
   * @return
   *   the converted instance
   */
  def from(b: B): A

object Iso:

  /**
   * Creates an isomorphism between two types `A` and `B` using the provided conversion functions.
   * @param toFn
   *   the function to convert from `A` to `B`
   * @param fromFn
   *   the function to convert from `B` to `A`
   * @return
   *   the isomorphism between `A` and `B`
   */
  inline def apply[A, B](inline toFn: A => B, inline fromFn: B => A): Iso[A, B] = IsoImpl(toFn, fromFn)

  class IsoImpl[A, B](val toFn: A => B, val fromFn: B => A) extends Iso[A, B]:
    override def to(a: A): B = toFn(a)
    override def from(b: B): A = fromFn(b)

  given [A, B](using iso: Iso[A, B]): Conversion[A, B] with
    inline def apply(a: A): B = iso.to(a)

  given [A, B](using iso: Iso[A, B]): Conversion[B, A] with
    inline def apply(b: B): A = iso.from(b)
end Iso
