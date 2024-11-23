package it.unibo.field4s.abstractions

trait Mappable[F[_]]:

  extension [A](a: F[A])
    /**
     * Maps a collection of elements of type A to a collection of elements of type B
     * @param f
     *   the function to apply to each element of the collection
     * @tparam B
     *   the type of the elements of the resulting collection
     * @return
     *   a collection of elements of type B
     */
    def map[B](f: A => B): F[B]
