package it.unibo.scafi.utils

import java.util.concurrent.Executors

import scala.concurrent.{ blocking, ExecutionContext, Future }
import scala.concurrent.duration.FiniteDuration

/**
 * JVM and Native platform-specific utilities.
 */
object Platform:

  def asyncOps: AsyncOperations = new AsyncOperations:
    given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    override def sleep(duration: FiniteDuration): Future[Unit] =
      Future(blocking(Thread.sleep(duration.toMillis)))
