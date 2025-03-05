package it.unibo.field4s.collections

import it.unibo.field4s.abstractions.boundaries.{ LowerBounded, UpperBounded }

/**
 * A safe iterable is an iterable that provides only safe operations. Safe operations are those that do not throw
 * exceptions when the iterable is empty. For example, the `head` method of the [[Iterable]] trait throws an exception
 * when the iterable is empty.
 * @tparam A
 *   the type of the elements of the iterable
 */
trait SafeIterable[+A]:

  private val iterable: Iterable[A] = new Iterable[A]:
    override def iterator: Iterator[A] = SafeIterable.this.iterator

  protected def iterator: Iterator[A]

  /**
   * Converts this safe iterable to an iterable.
   * @return
   *   an iterable
   */
  def toIterable: Iterable[A] = iterable

  override def toString: String = iterable.toString()

  override def hashCode(): Int = iterable.hashCode()

  /**
   * Returns the minimum element of this iterable, according to the ordering of the elements. If the iterable is empty,
   * returns the upper bound of the type of the elements.
   * @see
   *   [[UpperBounded]]
   * @tparam B
   *   the type of the elements
   * @return
   *   the minimum element of this iterable
   */
  def min[B >: A: {Ordering, UpperBounded}]: B =
    minOption.getOrElse(summon[UpperBounded[B]].upperBound)

  /**
   * Returns the maximum element of this iterable, according to the ordering of the elements. If the iterable is empty,
   * returns the lower bound of the type of the elements.
   * @see
   *   [[LowerBounded]]
   * @tparam B
   *   the type of the elements
   * @return
   *   the maximum element of this iterable
   */
  def max[B >: A: {Ordering, LowerBounded}]: B =
    maxOption.getOrElse(summon[LowerBounded[B]].lowerBound)

  export iterable.{
    collectFirst,
    copyToArray,
    corresponds,
    count,
    exists,
    find,
    fold,
    foldLeft,
    foldRight,
    forall,
    foreach,
    groupMapReduce,
    headOption,
    isEmpty,
    lastOption,
    maxByOption,
    maxOption,
    minByOption,
    minOption,
    mkString,
    nonEmpty,
    product,
    reduceLeftOption,
    reduceOption,
    reduceRightOption,
    size,
    sum,
    to,
    toArray,
    toBuffer,
    toIndexedSeq,
    toList,
    toMap,
    toSeq,
    toSet,
    toVector,
    view,
  }
end SafeIterable

object SafeIterable:

  def apply[T](underlying: Iterable[T]): SafeIterable[T] = new SafeIterable[T]:
    override protected def iterator: Iterator[T] = underlying.iterator
