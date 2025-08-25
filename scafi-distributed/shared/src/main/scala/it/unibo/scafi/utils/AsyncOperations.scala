package it.unibo.scafi.utils

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
 * Defines asynchronous utilities operations, implemented in a non-blocking way to be effectively used in non-blocking
 * platforms, e.g., Scala.js.
 */
trait AsyncOperations:

  /**
   * Sleeps for the specified duration.
   * @param duration
   *   the duration to sleep
   * @return
   *   a Future that completes after the specified duration
   */
  def sleep(duration: FiniteDuration): Future[Unit]

/**
 * Platform-specific [[AsyncOperations]] provider.
 */
object Async:

  /** Provides access to platform-specific asynchronous operations. */
  def operations: AsyncOperations = Platform.asyncOps
