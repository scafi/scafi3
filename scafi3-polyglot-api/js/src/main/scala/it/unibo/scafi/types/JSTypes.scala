package it.unibo.scafi.types

import scala.concurrent.{ ExecutionContext, Future }
import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichIterableOnce
import scala.util.{ Failure, Success }

/**
 * Provides JavaScript-specific implementations for the portable types.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait JSTypes extends PortableTypes:

  override type Map[K, V] = js.Map[K, V]
  override given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = Iso(_.toMap, m => js.Map(m.toSeq*))

  override type Seq[V] = js.Array[V]
  override given [V] => Iso[Seq[V], collection.Seq[V]] = Iso(identity, _.toJSArray)

  override type Outcome[T] = js.Promise[T] | T
  override given [T] => Iso[Outcome[T], Future[T]] = Iso(
    {
      case p: js.Promise[?] => p.toFuture.asInstanceOf[Future[T]]
      case v => Future.successful(v.asInstanceOf[T])
    },
    f =>
      given ExecutionContext = scalajs.concurrent.JSExecutionContext.queue
      js.Promise[T]: (resolve, reject) =>
        f.onComplete:
          case Success(value) => resolve(value)
          case Failure(exception) => reject(exception),
  )

  override type Function0[R] = js.Function0[R]
  given toScalaFunction0[R]: Conversion[Function0[R], () => R] = _.apply

  override type Function1[T1, R] = js.Function1[T1, R]
  given toScalaFunction1[T1, R]: Conversion[Function1[T1, R], T1 => R] = _.apply

  override type Function2[T1, T2, R] = js.Function2[T1, T2, R]
  given toScalaFunction2[T1, T2, R]: Conversion[Function2[T1, T2, R], (T1, T2) => R] = _.apply

end JSTypes
