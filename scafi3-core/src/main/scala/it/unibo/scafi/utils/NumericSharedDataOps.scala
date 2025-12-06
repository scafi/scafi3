package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

import cats.syntax.all.*

object NumericSharedDataOps:
  extension [V: Numeric as numeric](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def +(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(numeric.plus)
    infix def -(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(numeric.minus)
    infix def *(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(numeric.times)
    def unary_- : fieldData.SharedData[V] = data.map(numeric.negate)
    def toInt: fieldData.SharedData[Int] = data.map(numeric.toInt)
    def toLong: fieldData.SharedData[Long] = data.map(numeric.toLong)
    def toDouble: fieldData.SharedData[Double] = data.map(numeric.toDouble)
    def abs: fieldData.SharedData[V] = data.map(numeric.abs)

  extension [V: Fractional as fractional](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def /(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(fractional.div)

  extension [V: Integral as integral](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def /(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(integral.quot)
    infix def %(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(integral.rem)
    infix def /%(that: fieldData.SharedData[V]): fieldData.SharedData[(V, V)] =
      (data, that).mapN((a, b) => (integral.quot(a, b), integral.rem(a, b)))
