package it.unibo.scafi.types

import scala.collection.mutable
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import scala.scalanative.unsafe.{ CFuncPtr0, CFuncPtr1, CFuncPtr2, Ptr }
import scala.scalanative.unsigned.toCSize

import it.unibo.scafi.nativebindings.all.Array as CArray
import it.unibo.scafi.utils.CUtils.freshPointer

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait NativeTypes extends PortableTypes:

  override type Map[K, V] = Ptr[Byte]
  override given [K, V] => Iso[Map[K, V], collection.Map[K, V]] = Iso(CMap.of(_).toMap, m => CMap(mutable.Map.from(m)))

  override type Seq[V] = Ptr[CArray]
  override given [V] => Iso[Seq[V], collection.Seq[V]] = Iso(
    cArray => for i <- 0 until (!cArray).size.toInt yield ((!cArray).items(i).asInstanceOf[V]),
    scalaSeq =>
      val arr = freshPointer[CArray]
      (!arr).items = freshPointer[Ptr[Byte]](scalaSeq.length)
      scalaSeq.zipWithIndex.foreach: (v, i) =>
        (!arr).items(i) = v.asInstanceOf[Ptr[Byte]]
      (!arr).size = scalaSeq.length.toCSize
      arr,
  )

  override type Outcome[T] = T
  override given [T] => Iso[Outcome[T], Future[T]] = Iso(Future.successful, Await.result(_, Duration.Inf))

  /* WARNING
   * =======
   * Note that the following conversions for portable functions needs to be inlined to work properly since
   * the native compiler needs to generate type information (`Tag` type class) in the call site for the conversion
   * to work properly.
   * If the `inline` keyword is removed, compilation completes successfully but runtime execution leads to segfaults!
   */

  override type Function0[R] = CFuncPtr0[R]
  given toScalaFunction0[R]: Conversion[Function0[R], () => R] with
    inline def apply(f: Function0[R]): () => R = f.apply

  override type Function1[T1, R] = CFuncPtr1[T1, R]
  given toScalaFunction1[T1, R]: Conversion[Function1[T1, R], T1 => R] with
    inline def apply(f: Function1[T1, R]): T1 => R = f.apply

  override type Function2[T1, T2, R] = CFuncPtr2[T1, T2, R]
  given toScalaFunction2[T1, T2, R]: Conversion[Function2[T1, T2, R], (T1, T2) => R] with
    inline def apply(f: Function2[T1, T2, R]): (T1, T2) => R = f.apply

end NativeTypes
