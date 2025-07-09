package it.unibo.scafi.utils

import scala.concurrent.Future

/**
 * A deferred computation that, when run, produces a `Future` result.
 */
opaque type Task[+T] = () => Future[T]

object Task:

  /**
   * Creates a new deferred task from the given computation.
   * @param computation
   *   the computation to be executed when the task is run.
   * @return
   *   a newly created `Task`.
   */
  def apply[T](computation: => Future[T]): Task[T] = () => computation

  extension [T](task: Task[T])
    /**
     * Unsafely runs the task, producing a `Future` result.
     * @return
     *   a `Future` that completes with the result of the task.
     */
    def run(): Future[T] = task()
