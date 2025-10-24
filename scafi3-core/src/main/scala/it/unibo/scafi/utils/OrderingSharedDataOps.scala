package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

object OrderingSharedDataOps:
  extension [V: Ordering as ordering](using lang: FieldBasedSharedData)(data: lang.SharedData[V])
    infix def compare(that: lang.SharedData[V]): lang.SharedData[Int] = data.alignedMap(that)(ordering.compare)
    infix def min(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(ordering.min)
    infix def max(that: lang.SharedData[V]): lang.SharedData[V] = data.alignedMap(that)(ordering.max)
    def max: V = data.withoutSelf.foldLeft(data.onlySelf)(ordering.max)
    def min: V = data.withoutSelf.foldLeft(data.onlySelf)(ordering.min)
