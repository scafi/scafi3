package it.unibo.scafi.utils

import java.util.concurrent.Executors

import scala.concurrent.{ blocking, ExecutionContext, Future }
import scala.concurrent.duration.FiniteDuration

/**
 * Defines a platform that supports asynchronous operations in a blocking manner.
 */
trait BlockingPlatform:

  def asyncOps: AsyncOperations = new AsyncOperations:
    given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    override def sleep(duration: FiniteDuration): Future[Unit] =
      Future(blocking(Thread.sleep(duration.toMillis)))
