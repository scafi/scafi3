package it.unibo.scafi.utils

import scala.concurrent.Future

import it.unibo.scafi.utils.Channel.ChannelClosedException

/**
 * The read-side projection of a channel one can poll or take items from in a non-blocking way, allowing to work in both
 * JVM, Native and JS platforms where blocking operations are not allowed.
 * @tparam T
 *   the type of items that can be read from the channel.
 */
trait ReadableChannel[+T]:

  /**
   * Removes and returns the head of this channel if available.
   * @return
   *   the head of this channel boxed in a `Some` if available, or `None` if the channel is empty.
   */
  def poll: Option[T]

  /**
   * Removes and returns the next item from the channel asynchronously as soon as it will be available.
   * @return
   *   a new [[Future]] that will be completed successfully with the next item from the channel as soon as it will be
   *   available. If the channel is closed and no more items can be taken, the future is completed with a failure of
   *   type [[ChannelClosedException]].
   */
  def take: Future[T]

/**
 * The write-side projection of a channel one can push items onto.
 * @tparam T
 *   the type of items that can be pushed onto the channel.
 */
trait SendableChannel[-T]:

  /**
   * Pushes an item onto the channel.
   * @param item
   *   the item to be pushed onto the channel.
   * @throws ChannelClosedException
   *   if the channel is closed.
   */
  def push(item: T): Unit throws ChannelClosedException

  /**
   * Closes the channel, preventing any further items from being pushed onto it. Polling or taking operations are still
   * allowed, until the channel is empty.
   * @throws ChannelClosedException
   *   if the channel was already closed.
   */
  def close(): Unit throws ChannelClosedException

  /**
   * Checks if the channel is closed.
   * @return
   *   true if the channel is closed, false otherwise.
   */
  def isClosed: Boolean
end SendableChannel

/**
 * A channel from which items can be read, pushed onto, and closed in a non-blocking way, allowing to work in both JVM,
 * Native and JS platforms where blocking operations are not allowed.
 */
trait Channel[T] extends ReadableChannel[T] with SendableChannel[T]:

  /**
   * Converts this channel to the read-side projection.
   */
  def asReadable: ReadableChannel[T] = this

  /**
   * Converts this channel to the write-side projection.
   */
  def asSendable: SendableChannel[T] = this

object Channel:
  /**
   * An exception thrown when a not allowed operation is attempted on a closed channel.
   * @param msg
   *   an optional message for providing additional context to the exception.
   */
  class ChannelClosedException(msg: String = "") extends Exception(s"Channel is closed. $msg")

  /**
   * Creates a new empty channel.
   */
  def apply[T]: Channel[T] = ChannelImpl[T]

  /**
   * Creates a new empty read-only channel.
   */
  def readable[T]: ReadableChannel[T] = apply.asReadable

  /**
   * Creates a new empty write-only channel.
   */
  def sendable[T]: SendableChannel[T] = apply.asSendable

  private class ChannelImpl[T] extends Channel[T]:
    import scala.concurrent.Promise
    import scala.util.chaining.scalaUtilChainingOps

    private var closed: Boolean = false
    private val buffer = collection.mutable.Queue.empty[T]
    private val waiters = collection.mutable.Queue.empty[Promise[T]]

    override def push(item: T): Unit throws ChannelClosedException = synchronized:
      if closed then throw ChannelClosedException("No more items can be pushed.")
      else if waiters.nonEmpty then waiters.dequeue.success(item): Unit
      else buffer.enqueue(item): Unit

    override def poll: Option[T] = synchronized:
      Option.when(buffer.nonEmpty)(buffer.dequeue())

    override def take: Future[T] = synchronized:
      poll match
        case Some(value) => Future.successful(value)
        case None if closed => Future.failed(ChannelClosedException("No more available items."))
        case None => Promise[T]().tap(waiters.enqueue).future

    override def close(): Unit throws ChannelClosedException = synchronized:
      if closed then throw ChannelClosedException()
      else
        closed = true
        waiters.drain.foreach(_.failure(ChannelClosedException()))

    extension [R](q: collection.mutable.Queue[R]) def drain: Seq[R] = q.dequeueAll(_ => true)

    override def isClosed: Boolean = synchronized(closed)
  end ChannelImpl
end Channel
