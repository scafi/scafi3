package it.unibo.scafi.utils

import it.unibo.scafi.language.AggregateFoundation

object PartialOrderingSharedDataOps:
  extension [V: PartialOrdering as ordering](using lang: AggregateFoundation)(data: lang.SharedData[V])
    infix def <(that: lang.SharedData[V]): lang.SharedData[Boolean] = data.alignedMap(that)(ordering.lt)
    infix def <=(that: lang.SharedData[V]): lang.SharedData[Boolean] = data.alignedMap(that)(ordering.lteq)
    infix def >(that: lang.SharedData[V]): lang.SharedData[Boolean] = data.alignedMap(that)(ordering.gt)
    infix def >=(that: lang.SharedData[V]): lang.SharedData[Boolean] = data.alignedMap(that)(ordering.gteq)
    infix def tryCompareTo(that: lang.SharedData[V]): lang.SharedData[Option[Int]] =
      data.alignedMap(that)(ordering.tryCompare)
    infix def equiv(that: lang.SharedData[V]): lang.SharedData[Boolean] = data.alignedMap(that)(ordering.equiv)
