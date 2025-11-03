package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

object PartialOrderingSharedDataOps:
  extension [V: PartialOrdering as ordering](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def <(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = data.alignedMap(that)(ordering.lt)
    infix def <=(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = data.alignedMap(that)(ordering.lteq)
    infix def >(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = data.alignedMap(that)(ordering.gt)
    infix def >=(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = data.alignedMap(that)(ordering.gteq)
    infix def tryCompareTo(that: fieldData.SharedData[V]): fieldData.SharedData[Option[Int]] =
      data.alignedMap(that)(ordering.tryCompare)
    infix def equiv(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] =
      data.alignedMap(that)(ordering.equiv)
