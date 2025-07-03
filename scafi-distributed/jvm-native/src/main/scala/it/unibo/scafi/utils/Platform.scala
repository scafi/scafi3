package it.unibo.scafi.utils

import scala.concurrent.{ blocking, ExecutionContext, Future }
import scala.concurrent.duration.FiniteDuration

object Platform:

  def asyncOps: AsyncOperations = new AsyncOperations:
    given ExecutionContext = ExecutionContext.global

    override def sleep(duration: FiniteDuration): Future[Unit] =
      Future(blocking(Thread.sleep(duration.toMillis)))
