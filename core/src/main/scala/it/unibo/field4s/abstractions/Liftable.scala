package it.unibo.field4s.abstractions

/**
 * A type class that provides ways to combine collections of values into new collections of values.
 * @tparam F
 *   the type of the collection
 */
trait Liftable[F[_]] extends Mappable[F]:
  /**
   * Maps a collection of values to a new collection of values by applying a function to each value.
   */
  def lift[A, B](a: F[A])(f: A => B): F[B]

  /**
   * Combines two collections of values into a new collection of values by applying a function to each pair of values.
   */
  def lift[A, B, C](a: F[A], b: F[B])(f: (A, B) => C): F[C]

  /**
   * Combines three collections of values into a new collection of values by applying a function to each triple of
   * values.
   */
  def lift[A, B, C, D](a: F[A], b: F[B], c: F[C])(f: (A, B, C) => D): F[D]

  extension [A](a: F[A]) override def map[B](f: A => B): F[B] = lift(a)(f)

end Liftable

object Liftable:

  /**
   * Maps a collection of values to a new collection of values by applying a function to each value.
   * @param a
   *   the collection of values
   * @param f
   *   the function to apply to each value
   * @tparam A
   *   the type of the values in the collection
   * @tparam B
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the new collection of values
   */
  def lift[A, B, F[_]: Liftable](a: F[A])(f: A => B): F[B] =
    summon[Liftable[F]].lift(a)(f)

  /**
   * Maps a collection of collections of values to a new collection of collections of values by applying a function to
   * each value.
   * @param a
   *   the collection of collections of values
   * @param f
   *   the function to apply to each value
   * @tparam A
   *   the type of the values in the collections
   * @tparam B
   *   the type of the values in the new collections
   * @tparam F1
   *   the type of the outer collection
   * @tparam F2
   *   the type of the inner collection
   * @return
   *   the new collection of collections of values
   */
  def liftTwice[A, B, F1[_]: Liftable, F2[_]: Liftable](a: F1[F2[A]])(
      f: A => B,
  ): F1[F2[B]] = lift(a)(aa => lift(aa)(f))

  /**
   * Combines two collections of values into a new collection of values by applying a function to each pair of values.
   * @param a
   *   the first collection of values
   * @param b
   *   the second collection of values
   * @param f
   *   the function to apply to each pair of values
   * @tparam A
   *   the type of the values in the first collection
   * @tparam B
   *   the type of the values in the second collection
   * @tparam C
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the new collection of values
   */
  def lift[A, B, C, F[_]: Liftable](a: F[A], b: F[B])(f: (A, B) => C): F[C] =
    summon[Liftable[F]].lift(a, b)(f)

  /**
   * Combines two collections of collections of values into a new collection of collections of values by applying a
   * function to each pair of values.
   * @param a
   *   the first collection of collections of values
   * @param b
   *   the second collection of collections of values
   * @param f
   *   the function to apply to each pair of values
   * @tparam A
   *   the type of the values in the first collections
   * @tparam B
   *   the type of the values in the second collections
   * @tparam C
   *   the type of the values in the new collections
   * @tparam F1
   *   the type of the outer collection
   * @tparam F2
   *   the type of the inner collection
   * @return
   *   the new collection of collections of values
   */
  def liftTwice[A, B, C, F1[_]: Liftable, F2[_]: Liftable](
      a: F1[F2[A]],
      b: F1[F2[B]],
  )(f: (A, B) => C): F1[F2[C]] =
    lift(a, b)((aa, bb) => lift(aa, bb)(f))

  /**
   * Combines three collections of values into a new collection of values by applying a function to each triple of
   * values.
   * @param a
   *   the first collection of values
   * @param b
   *   the second collection of values
   * @param c
   *   the third collection of values
   * @param f
   *   the function to apply to each triple of values
   * @tparam A
   *   the type of the values in the first collection
   * @tparam B
   *   the type of the values in the second collection
   * @tparam C
   *   the type of the values in the third collection
   * @tparam D
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the new collection of values
   */
  def lift[A, B, C, D, F[_]: Liftable](a: F[A], b: F[B], c: F[C])(
      f: (A, B, C) => D,
  ): F[D] =
    summon[Liftable[F]].lift(a, b, c)(f)

  /**
   * Combines three collections of collections of values into a new collection of collections of values by applying a
   * function to each triple of values.
   * @param a
   *   the first collection of collections of values
   * @param b
   *   the second collection of collections of values
   * @param c
   *   the third collection of collections of values
   * @param f
   *   the function to apply to each triple of values
   * @tparam A
   *   the type of the values in the first collections
   * @tparam B
   *   the type of the values in the second collections
   * @tparam C
   *   the type of the values in the third collections
   * @tparam D
   *   the type of the values in the new collections
   * @tparam F1
   *   the type of the outer collection
   * @tparam F2
   *   the type of the inner collection
   * @return
   *   the new collection of collections of values
   */
  def liftTwice[A, B, C, D, F1[_]: Liftable, F2[_]: Liftable](
      a: F1[F2[A]],
      b: F1[F2[B]],
      c: F1[F2[C]],
  )(
      f: (A, B, C) => D,
  ): F1[F2[D]] =
    lift(a, b, c)((aa, bb, cc) => lift(aa, bb, cc)(f))

  /**
   * Lifts a function to operate on collections of values.
   * @param f
   *   the function to lift
   * @tparam A
   *   the type of the first value
   * @tparam B
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the lifted function
   */
  def lift[A, B, F[_]: Liftable](f: A => B): F[A] => F[B] = a => lift(a)(f)

  /**
   * Lifts a function to operate on two collections of values.
   * @param f
   *   the function to lift
   * @tparam A
   *   the type of the first value
   * @tparam B
   *   the type of the second value
   * @tparam C
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the lifted function
   */
  def lift[A, B, C, F[_]: Liftable](f: (A, B) => C): (F[A], F[B]) => F[C] =
    (a, b) => lift(a, b)(f)

  /**
   * Lifts a function to operate on three collections of values.
   * @param f
   *   the function to lift
   * @tparam A
   *   the type of the first value
   * @tparam B
   *   the type of the second value
   * @tparam C
   *   the type of the third value
   * @tparam D
   *   the type of the values in the new collection
   * @tparam F
   *   the type of the collection
   * @return
   *   the lifted function
   */
  def lift[A, B, C, D, F[_]: Liftable](
      f: (A, B, C) => D,
  ): (F[A], F[B], F[C]) => F[D] = (a, b, c) => lift(a, b, c)(f)
end Liftable
