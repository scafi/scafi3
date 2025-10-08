package it.unibo.scafi.utils.boundaries

import scala.quoted.{ Expr, Quotes, Type }

object BoundedGenerator:
  inline def generateBoundedForTuples[T](using ord: Ordering[T]): Bounded[T] =
    ${ generateBoundedForNumbersImpl[T](using 'ord) }

  private def generateBoundedForNumbersImpl[T: Type](using
      ord: Expr[Ordering[T]],
  )(using quotes: Quotes): Expr[Bounded[T]] =
    '{
      given Ordering[T] = $ord
      new Bounded[T]:
        override def lowerBound: T = ???
        override def upperBound: T = ???
    }
