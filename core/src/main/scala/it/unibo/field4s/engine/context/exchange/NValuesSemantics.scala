package it.unibo.field4s.engine.context.exchange

import scala.collection.MapView

import it.unibo.field4s.abstractions.{ Aggregate, Liftable }
import it.unibo.field4s.collections.SafeIterable
import it.unibo.field4s.language.semantics.exchange.{ ExchangeCalculusSemantics, NValuesOps }

/**
 * Implements the foundational semantics for the NValues of the exchange calculus.
 */
trait NValuesSemantics:
  this: ExchangeCalculusSemantics =>
  override type AggregateValue[T] = NValues[T]

  /**
   * A NValues is a mapping from device ids to values of type T. For devices not aligned with the current device, the
   * default value is used.
   * @param default
   *   the default value for unaligned devices
   * @param unalignedValues
   *   the values for all devices, aligned and unaligned
   * @tparam T
   *   the type of the values
   */
  protected case class NValues[+T](default: T, unalignedValues: Map[DeviceId, T] = Map.empty) extends SafeIterable[T]:

    /**
     * @return
     *   a filtered view of the NValues data that only contains the values for aligned devices
     */
    def alignedValues: MapView[DeviceId, T] =
      alignedDevices.view.map(id => id -> unalignedValues.getOrElse(id, default)).toMap.view

    /**
     * @param id
     *   the device id
     * @return
     *   the value for the given device id, or the default value if the device is not aligned
     */
    def apply(id: DeviceId): T = alignedValues.getOrElse(id, default)

    override def iterator: Iterator[T] = alignedValues.values.iterator
    override def toString: String = s"NValues($default, $unalignedValues)"
  end NValues

  override given nvalues: NValuesOps[AggregateValue, DeviceId] = new NValuesOps[AggregateValue, DeviceId]:

    extension [T](nv: AggregateValue[T])
      override def default: T = nv.default
      override def values: MapView[DeviceId, T] = nv.alignedValues

      override def set(id: DeviceId, value: T): AggregateValue[T] = NValues[T](
        nv.default,
        nv.unalignedValues + (id -> value),
      )

  override given liftable: Liftable[AggregateValue] = new Liftable[AggregateValue]:

    override def lift[A, B](a: NValues[A])(f: A => B): NValues[B] =
      new NValues[B](f(a.default), a.unalignedValues.view.mapValues(f).toMap)

    override def lift[A, B, C](a: NValues[A], b: NValues[B])(f: (A, B) => C): NValues[C] =
      new NValues[C](
        f(a.default, b.default),
        (a.unalignedValues.keySet ++ b.unalignedValues.keySet).map(k => k -> f(a(k), b(k))).toMap,
      )

    override def lift[A, B, C, D](a: NValues[A], b: NValues[B], c: NValues[C])(
        f: (A, B, C) => D,
    ): NValues[D] = NValues[D](
      f(a.default, b.default, c.default),
      (a.unalignedValues.keySet ++ b.unalignedValues.keySet ++ c.unalignedValues.keySet)
        .map(k => k -> f(a(k), b(k), c(k)))
        .toMap,
    )

  override given aggregate: Aggregate[AggregateValue] = new Aggregate[AggregateValue]:

    extension [A](a: AggregateValue[A])

      override def withoutSelf: SafeIterable[A] =
        val filtered = a.alignedValues.filterKeys(_ != self).values
        SafeIterable(filtered)
      override def onlySelf: A = a(self)

  override given convert[T]: Conversion[T, AggregateValue[T]] = NValues[T](_)

  override def device: AggregateValue[DeviceId] = NValues[DeviceId](self, alignedDevices.map(id => (id, id)).toMap)

  /**
   * @return
   *   the set of device ids that are aligned with the current device
   */
  protected def alignedDevices: Set[DeviceId]
end NValuesSemantics
