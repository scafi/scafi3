package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

object NumericSharedDataOps:
  extension [V: Numeric as numeric](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def +(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(numeric.plus)
    infix def -(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(numeric.minus)
    infix def *(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(numeric.times)
    def unary_- : fieldData.SharedData[V] = data.mapValues(numeric.negate)
    def toInt: fieldData.SharedData[Int] = data.mapValues(numeric.toInt)
    def toLong: fieldData.SharedData[Long] = data.mapValues(numeric.toLong)
    def toDouble: fieldData.SharedData[Double] = data.mapValues(numeric.toDouble)
    def abs: fieldData.SharedData[V] = data.mapValues(numeric.abs)

  extension [V: Fractional as fractional](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def /(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(fractional.div)

  extension [V: Integral as integral](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def /(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(integral.quot)
    infix def %(that: fieldData.SharedData[V]): fieldData.SharedData[V] = data.alignedMap(that)(integral.rem)
    infix def /%(that: fieldData.SharedData[V]): fieldData.SharedData[(V, V)] =
      data.alignedMap(that)((a, b) => (integral.quot(a, b), integral.rem(a, b)))
