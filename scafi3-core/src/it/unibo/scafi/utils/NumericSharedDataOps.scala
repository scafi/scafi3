package it.unibo.scafi.utils

import it.unibo.scafi.language.AggregateFoundation

object NumericSharedDataOps:
  extension [V: Numeric as numeric](using lang: AggregateFoundation)(data: lang.SharedData[V])
    infix def +(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(numeric.plus)
    infix def -(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(numeric.minus)
    infix def *(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(numeric.times)
    def unary_- : lang.SharedData[V] = data.mapValues(numeric.negate)
    def toInt: lang.SharedData[Int] = data.mapValues(numeric.toInt)
    def toLong: lang.SharedData[Long] = data.mapValues(numeric.toLong)
    def toDouble: lang.SharedData[Double] = data.mapValues(numeric.toDouble)
    def abs: lang.SharedData[V] = data.mapValues(numeric.abs)

  extension [V: Fractional as fractional](using lang: AggregateFoundation)(data: lang.SharedData[V])
    infix def /(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(fractional.div)

  extension [V: Integral as integral](using lang: AggregateFoundation)(data: lang.SharedData[V])
    infix def /(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(integral.quot)
    infix def %(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(integral.rem)
    infix def /%(that: lang.SharedData[V]): lang.SharedData[(V, V)] =
      data.alignedMap(that)((a, b) => (integral.quot(a, b), integral.rem(a, b)))
