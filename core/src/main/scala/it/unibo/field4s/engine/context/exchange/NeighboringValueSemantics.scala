package it.unibo.field4s.engine.context.exchange

import scala.collection.MapView

import it.unibo.field4s.abstractions.SharedDataOps
import it.unibo.field4s.collections.SafeIterable

import cats.Applicative
import it.unibo.field4s.language.exchange.semantics.{ExchangeCalculusSemantics, FieldOps}

/**
 * Implements the foundational semantics for the NValues of the exchange calculus.
 */
trait NeighboringValueSemantics:
  this: ExchangeCalculusSemantics =>
  override type SharedData[T] = Field[T]

  /**
   * A Field (NValue in https://doi.org/10.1016/j.jss.2024.111976) is a mapping from device ids to values of type T. For devices not aligned with the current device, the
   * default value is used.
   * @param default
   *   the default value for unaligned devices
   * @param unalignedValues
   *   the values for all devices, aligned and unaligned
   * @tparam T
   *   the type of the values
   */
  protected case class Field[+T](default: T, unalignedValues: Map[DeviceId, T] = Map.empty) extends SafeIterable[T]:

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
  end Field

  override given fieldOps: FieldOps[SharedData, DeviceId] =
    new FieldOps[SharedData, DeviceId]:

      extension [T](nv: SharedData[T])
        override def default: T = nv.default
        override def values: MapView[DeviceId, T] = nv.alignedValues

        override def set(id: DeviceId, value: T): SharedData[T] = Field[T](
          nv.default,
          nv.unalignedValues + (id -> value),
        )

  override given sharedDataApplicative: Applicative[SharedData] = new Applicative[SharedData]:
    override def pure[A](x: A): Field[A] = Field(x, Map.empty)

    override def ap[A, B](ff: Field[A => B])(fa: Field[A]): Field[B] = Field(
      ff.default(fa.default),
      (ff.unalignedValues.keySet ++ fa.unalignedValues.keySet)
        .map(deviceId => deviceId -> ff(deviceId)(fa(deviceId)))
        .toMap,
    )

    override def map[A, B](fa: Field[A])(f: A => B): Field[B] = Field[B](
      f(fa.default),
      fa.unalignedValues.view.mapValues(f).toMap,
    )

  override given sharedDataOps: SharedDataOps[SharedData] = new SharedDataOps[SharedData]:

    extension [A](a: SharedData[A])

      override def withoutSelf: SafeIterable[A] =
        val filtered = a.alignedValues.filterKeys(_ != self).values
        SafeIterable(filtered)
      override def onlySelf: A = a(self)

  override given convert[T]: Conversion[T, SharedData[T]] = Field[T](_)

  override def device: SharedData[DeviceId] = Field[DeviceId](self, alignedDevices.map(id => (id, id)).toMap)

  /**
   * @return
   *   the set of device ids that are aligned with the current device
   */
  protected def alignedDevices: Set[DeviceId]
end NeighboringValueSemantics
