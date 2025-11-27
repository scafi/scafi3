package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

import cats.syntax.all.*

object OrderingSharedDataOps:
  extension [V: Ordering as ordering](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def compare(that: fieldData.SharedData[V]): fieldData.SharedData[Int] =
      (data, that).mapN(ordering.compare)
    infix def min(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(ordering.min)
    infix def max(that: fieldData.SharedData[V]): fieldData.SharedData[V] = (data, that).mapN(ordering.max)
    def max: V = data.withoutSelf.foldLeft(data.onlySelf)(ordering.max)
    def min: V = data.withoutSelf.foldLeft(data.onlySelf)(ordering.min)
