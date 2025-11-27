package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

import cats.syntax.all.*

object PartialOrderingSharedDataOps:
  extension [V: PartialOrdering as ordering](using fieldData: FieldBasedSharedData)(data: fieldData.SharedData[V])
    infix def <(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = (data, that).mapN(ordering.lt)
    infix def <=(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = (data, that).mapN(ordering.lteq)
    infix def >(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = (data, that).mapN(ordering.gt)
    infix def >=(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] = (data, that).mapN(ordering.gteq)
    infix def tryCompareTo(that: fieldData.SharedData[V]): fieldData.SharedData[Option[Int]] =
      (data, that).mapN(ordering.tryCompare)
    infix def equiv(that: fieldData.SharedData[V]): fieldData.SharedData[Boolean] =
      (data, that).mapN(ordering.equiv)
