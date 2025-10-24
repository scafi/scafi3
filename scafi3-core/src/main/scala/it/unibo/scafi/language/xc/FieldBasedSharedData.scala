package it.unibo.scafi.language.xc

import scala.collection.MapView

import it.unibo.scafi.collections.SafeIterable
import it.unibo.scafi.language.ShareDataOps
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus
import it.unibo.scafi.utils.SharedDataOps

import cats.Applicative

/**
 * Implements the foundational semantics for the NValues of the exchange calculus.
 */
trait FieldBasedSharedData:
  this: ExchangeCalculus =>
  override type SharedData[T] = Field[T]

  /**
   * A Field (NValue in https://doi.org/10.1016/j.jss.2024.111976) is a mapping from device ids to values of type T. For
   * devices not aligned with the current device, the default value is used.
   * @param default
   *   the default value for unaligned devices
   * @param neighborValues
   *   the values for all devices, aligned and unaligned
   * @tparam Value
   *   the type of the values
   */
  protected case class Field[+Value](
      default: Value,
      neighborValues: Map[DeviceId, Value] = Map.empty,
  ) extends SafeIterable[Value]:

    /**
     * @return
     *   a filtered view of the NValues data that only contains the values for aligned devices
     */
    def alignedValues: Map[DeviceId, Value] =
      if neighborValues.isEmpty then Map(localId -> default) // self is always aligned, even if there are no neighbors
      else if alignedDevices.size == neighborValues.size then
        neighborValues // all devices are aligned, there is no need to filter
      else
        alignedDevices
          .map(id => id -> neighborValues.getOrElse(id, default))
          .toMap // in all other cases, I need to filter based on the aligned devices

    /**
     * @param id
     *   the device id
     * @return
     *   the value for the given device id, or the default value if the device is not aligned
     */
    def apply(id: DeviceId): Value = alignedValues.getOrElse(id, default)

    override def iterator: Iterator[Value] = alignedDevices
      .map(id => neighborValues.getOrElse(id, default))
      .iterator
    override def toString: String = s"Field($default, $neighborValues)"
  end Field

  /**
   * @return
   *   the set of device ids that are aligned with the current device
   */
  protected def alignedDevices: Iterable[DeviceId]

  override given fieldOps: ShareDataOps[SharedData, DeviceId] = new ShareDataOps[SharedData, DeviceId]:
    extension [T](nv: Field[T])
      override def default: T = nv.default
      override def values: MapView[DeviceId, T] = nv.alignedValues.view
      override def set(id: DeviceId, value: T): SharedData[T] = Field[T](
        nv.default,
        nv.neighborValues + (id -> value),
      )

  override given sharedDataApplicative: Applicative[SharedData] = new Applicative[SharedData]:
    override def pure[A](x: A): Field[A] = Field(x, Map.empty)

    override def ap[A, B](ff: Field[A => B])(fa: Field[A]): Field[B] = Field(
      ff.default(fa.default),
      (ff.neighborValues.keySet ++ fa.neighborValues.keySet)
        .map(deviceId => deviceId -> ff(deviceId)(fa(deviceId)))
        .toMap,
    )

    override def map[A, B](fa: Field[A])(f: A => B): Field[B] = Field[B](
      f(fa.default),
      fa.neighborValues.view.mapValues(f).toMap,
    )

  override given sharedDataOps: SharedDataOps[SharedData] = new SharedDataOps[SharedData]:
    extension [A](a: Field[A])
      override def withoutSelf: SafeIterable[A] =
        val filtered = a.alignedValues.view.filterKeys(_ != localId).values
        SafeIterable(filtered)
      override def onlySelf: A = a(localId)

      override def mapValues[B](f: A => B): SharedData[B] = Field[B](
        f(a.default),
        a.neighborValues.view.mapValues(f).toMap,
      )

      override def alignedMap[B, C](other: SharedData[B])(f: (A, B) => C): SharedData[C] =
        require(
          a.neighborValues.keySet.diff(other.neighborValues.keySet).isEmpty,
          s"Cannot alignedMap fields with different aligned devices: ${a.neighborValues.keySet} vs ${other.neighborValues.keySet}",
        )
        Field[C](
          f(a.default, other.default),
          a.neighborValues.view.map { case (id, value) => id -> f(value, other(id)) }.toMap,
        )
    end extension

  override given convert[T]: Conversion[T, SharedData[T]] = Field[T](_)

  override def device: SharedData[DeviceId] = Field[DeviceId](localId, alignedDevices.map(id => (id, id)).toMap)
end FieldBasedSharedData
