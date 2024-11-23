package it.unibo.field4s.abstractions

import it.unibo.field4s.collections.SafeIterable

/**
 * Type class testifying that a collection of elements is an aggregate of distributed values coming from neighbours. It
 * provides extension methods to work with the aggregate.
 * @tparam F
 *   the type of the collection
 */
trait Aggregate[F[A] <: SafeIterable[A]]:

  extension [A](a: F[A])
    /**
     * @return
     *   a view of this aggregate value without the value of the "self" node
     */
    def withoutSelf: SafeIterable[A]

    /**
     * @return
     *   the value of the "self" node
     */
    def onlySelf: A
