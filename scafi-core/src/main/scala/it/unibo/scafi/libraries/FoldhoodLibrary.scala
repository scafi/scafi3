package it.unibo.scafi.libraries

import it.unibo.scafi.sensors.DistanceSensor.senseDistance
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.sensors.DistanceSensor

import cats.syntax.all.*

import FieldCalculusLibrary.neighborValues as fcNbr
import FoldingLibrary.foldWithoutSelf

object FoldhoodLibrary:

  /**
   * The nbr construct is used to access the corresponding values of the neighbors during the evaluation of a foldhood
   * expression.
   * @param expr
   *   the expression to be evaluated, once for self and once for each neighbor
   * @param c
   *   the foldhood context
   * @tparam A
   *   the type of the expression
   * @tparam L
   *   the language context
   * @return
   *   the value of the expression
   */
  def nbr[A, L <: AggregateFoundation & FieldCalculusSyntax](expr: => A)(using c: FoldhoodContext[L]): A =
    c.current(fcNbr[A](expr))

  /**
   * The distances construct is used to access the distance from the corresponding neighbors during the evaluation of a
   * foldhood expression.
   * @param c
   *   the foldhood context
   * @tparam N
   *   the type of the distance
   * @tparam L
   *   the language context
   * @return
   *   the distance from the neighbor
   */
  def distances[N, L <: AggregateFoundation & FieldCalculusSyntax & DistanceSensor[N]](using
      Numeric[N],
  )(using
      c: FoldhoodContext[L],
  ): N = c.current(senseDistance)

  /**
   * The foldhood construct is used to aggregate the results of an expression over the neighborhood, excluding self.
   * @param base
   *   the initial value of the aggregation
   * @param f
   *   the aggregation function
   * @param expr
   *   the expression to be evaluated, once for self and once for each neighbor
   * @param language
   *   the language context
   * @tparam A
   *   the type of the expression
   * @tparam B
   *   the type of the aggregation
   * @tparam L
   *   the language context
   * @return
   *   the aggregated value
   */
  def foldhood[A, B, L <: AggregateFoundation & FieldCalculusSyntax](
      base: B,
  )(f: (B, A) => B)(expr: FoldhoodContext[L] ?=> A)(using
      language: L,
  ): B = foldhoodImpl(false)(base)(f)(expr)

  /**
   * The foldhoodPlus construct is used to aggregate the results of an expression over the neighborhood, including self.
   * @param base
   *   the initial value of the aggregation
   * @param f
   *   the aggregation function
   * @param expr
   *   the expression to be evaluated, once for self and once for each neighbor
   * @param language
   *   the language context
   * @tparam A
   *   the type of the expression
   * @tparam B
   *   the type of the aggregation
   * @tparam L
   *   the language context
   * @return
   *   the aggregated value
   */
  def foldhoodWithoutSelf[A, B, L <: AggregateFoundation & FieldCalculusSyntax](
      base: B,
  )(f: (B, A) => B)(expr: FoldhoodContext[L] ?=> A)(using
      language: L,
  ): B = foldhoodImpl(true)(base)(f)(expr)

  private def foldhoodImpl[A, B, L <: AggregateFoundation & FieldCalculusSyntax](
      withSelf: Boolean,
  )(base: B)(f: (B, A) => B)(expr: FoldhoodContext[L] ?=> A)(using
      lang: L,
  ): B =
    var neighbouringValues: List[lang.SharedData[Any | Null]] = List.empty
    val initial: FoldhoodContext[L] = new FoldhoodContext[L]:
      override def current[X](expr: (lang: L) ?=> lang.SharedData[X]): X =
        val value: lang.SharedData[X] = expr
        neighbouringValues = value.map[Any | Null](x => x) :: neighbouringValues
        value.onlySelf
    var zippedNeighbouringValues: lang.SharedData[List[Any | Null]] = fcNbr(List.empty[Any | Null])
    val selfExprValue: A = expr(using initial)
    for nv <- neighbouringValues do
      zippedNeighbouringValues = (zippedNeighbouringValues, nv).mapN((list, value) => value :: list)
    zippedNeighbouringValues.foldWithoutSelf(if withSelf then f(base, selfExprValue) else base): (acc, values) =>
      val iterator = values.iterator
      val context: FoldhoodContext[L] = new FoldhoodContext[L]:
        override def current[X](expr: (lang: L) ?=> lang.SharedData[X]): X = iterator.next match
          case x: X @unchecked => x
          case _ => throw new ClassCastException("Type mismatch")
      val result = expr(using context)
      f(acc, result)
  end foldhoodImpl

  sealed abstract class FoldhoodContext[L <: AggregateFoundation]:
    private[FoldhoodLibrary] def current[A](expr: (lang: L) ?=> lang.SharedData[A]): A
end FoldhoodLibrary
