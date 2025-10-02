package it.unibo.scafi.libraries

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

/**
 * Provides JavaScript-specific implementations for the portable types.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait JSTypes extends PortableTypes:
  import scalajs.js

  override type Map[K, V] = js.Map[K, V]
  override given [K, V] => Iso[Map[K, V], collection.Map[K, V]] =
    Iso[Map[K, V], collection.Map[K, V]](_.toMap)(m => js.Map(m.toSeq*))

  override type Tuple2[A, B] = js.Tuple2[A, B]
  override given [A, B] => Iso[Tuple2[A, B], (A, B)] = Iso[Tuple2[A, B], (A, B)](identity)(identity)

  override type Function0[R] = js.Function0[R]
  override given [R] => Conversion[Function0[R], () => R] = _.apply

  override type Function1[T1, R] = js.Function1[T1, R]
  override given [T1, R] => Conversion[Function1[T1, R], T1 => R] = _.apply

  override type Outcome[T] = js.Promise[T] | T
  override given [T] => Iso[Outcome[T], Future[T]] = Iso[Outcome[T], Future[T]] {
    case p: js.Promise[?] => p.toFuture.asInstanceOf[Future[T]]
    case v => Future.successful(v.asInstanceOf[T])
  }(f =>
    given ExecutionContext = scalajs.concurrent.JSExecutionContext.queue
    js.Promise[T]: (resolve, reject) =>
      f.onComplete:
        case Success(value) => resolve(value)
        case Failure(exception) => reject(exception),
  )
end JSTypes
