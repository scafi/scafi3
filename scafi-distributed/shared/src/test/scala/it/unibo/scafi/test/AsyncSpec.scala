package it.unibo.scafi.test

import java.util.concurrent.TimeoutException

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

import it.unibo.scafi.utils.Async

import org.scalatest.compatible.Assertion
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should

/**
 * A base trait for asynchronous non-blocking cross-platform tests, needed to support platforms like Scala.js where
 * blocking operations are not allowed.
 */
trait AsyncSpec extends AsyncFlatSpec with should.Matchers with PatienceConfiguration:

  /**
   * Asynchronously and periodically verifies that the given assertion eventually holds true within the specified
   * patience configuration.
   * @param assertion
   *   the assertion to be verified
   * @param patience
   *   the contextually provided patience configuration
   * @return
   *   a `Future` that completes with the assertion if it holds true, or fails with a `TimeoutException` if the
   *   assertion does not hold within the specified timeout.
   */
  def eventually(assertion: => Assertion)(using patience: PatienceConfig): Future[Assertion] =
    val deadline = patience.timeout.fromNow
    def poll(): Future[Assertion] = Future(assertion).recoverWith:
      case _ if !deadline.isOverdue() => after(patience.interval)(poll())
      case e => Future.failed(TimeoutException(s"Condition not met in ${patience.timeout.toMillis}ms: ${e.getMessage}"))
    poll()

  /**
   * Delays the execution of the given operation by the specified duration.
   * @param delay
   *   the duration to wait before executing the operation
   * @param todo
   *   the operation to be executed after the delay
   * @tparam T
   *   the type of the result of the operation
   * @return
   *   a `Future` that completes with the result of the operation after the specified delay.
   */
  def after[T](delay: FiniteDuration)(todo: => Future[T]): Future[T] =
    Async.operations.sleep(delay).flatMap(_ => todo)

  extension [T](f: Future[T])
    infix def verify(assertion: T => Assertion): Future[Assertion] = f.map(assertion)
    infix def verifying(assertion: => Future[Assertion]): Future[Assertion] = verifying(_ => assertion)
    infix def verifying(assertion: T => Future[Assertion]): Future[Assertion] = f.flatMap(assertion)
end AsyncSpec
