package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.TimeoutException

import scala.concurrent.duration.*
import scala.concurrent.Future

import it.unibo.scafi.utils.Async

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should
import org.scalatest.compatible.Assertion
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.{ Millis, Seconds, Span }

trait NetworkingTest extends AsyncFlatSpec with should.Matchers with PatienceConfiguration:

  given PatienceConfig = PatienceConfig(timeout = Span(1, Seconds), interval = Span(100, Millis))

  def eventually(assertion: => Assertion)(using patience: PatienceConfig): Future[Assertion] =
    val deadline = patience.timeout.fromNow
    def poll(): Future[Assertion] = Future(assertion).recoverWith:
      case _ if !deadline.isOverdue() => after(patience.interval)(poll())
      case e => Future.failed(TimeoutException(s"Condition not met in ${patience.timeout.toMillis}ms: ${e.getMessage}"))
    poll()

  def after[T](duration: FiniteDuration)(todo: => Future[T]): Future[T] =
    Async.operations.sleep(duration).flatMap(_ => todo)

  extension [T](f: Future[T])
    infix def verify(assertion: T => Assertion): Future[Assertion] = f.map(assertion)
    infix def verifying(assertion: => Future[Assertion]): Future[Assertion] = verifying(_ => assertion)
    infix def verifying(assertion: T => Future[Assertion]): Future[Assertion] = f.flatMap(assertion)
end NetworkingTest
