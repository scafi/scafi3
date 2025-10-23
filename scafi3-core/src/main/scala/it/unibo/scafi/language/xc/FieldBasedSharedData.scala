package it.unibo.scafi.language.xc

import it.unibo.scafi.collections.SafeIterable
import it.unibo.scafi.language.SharedDataOps
import it.unibo.scafi.language.xc.calculus.ExchangeCalculus

import cats.Applicative

/**
 * Implements the foundational semantics for the [[SharedData]] of the exchange calculus.
 */
trait FieldBasedSharedData:
  this: ExchangeCalculus =>
  override type SharedData[Value] = Field[Value]

  /**
   * A Field (NValue in https://doi.org/10.1016/j.jss.2024.111976) is a mapping from device ids to values of type T. For
   * devices not aligned with the current device, the default value is used.
   * @param defaultValue
   *   the default value for unaligned devices
   * @param neighborValues
   *   the values for all devices, aligned and unaligned
   * @tparam Value
   *   the type of the values
   */
  protected case class Field[+Value](
      private[xc] val defaultValue: Value,
      private[xc] val neighborValues: Map[DeviceId, Value] = Map.empty,
  ) extends SafeIterable[Value]:

    /**
     * @return
     *   a filtered view of the [[SharedData]] data that only contains the values for aligned devices
     */
    def alignedValues: Map[DeviceId, Value] =
      if neighborValues.isEmpty then
        Map(localId -> defaultValue) // self is always aligned, even if there are no neighbors
      else if alignedDevices.size == neighborValues.size then
        neighborValues // all devices are aligned, there is no need to filter
      else
        alignedDevices
          .map(id => id -> neighborValues.getOrElse(id, defaultValue))
          .toMap // in all other cases, I need to filter based on the aligned devices

    /**
     * @param id
     *   the device id
     * @return
     *   the value for the given device id, or the default value if the device is not aligned
     */
    def apply(id: DeviceId): Value = alignedValues.getOrElse(id, defaultValue)

    override def iterator: Iterator[Value] = alignedDevices
      .map(id => neighborValues.getOrElse(id, defaultValue))
      .iterator
    override def toString: String = s"Field($defaultValue, $neighborValues)"
  end Field

  /**
   * @return
   *   the set of device ids that are aligned with the current device
   */
  protected def alignedDevices: Iterable[DeviceId]

  override given neighborValuesOps: NeighborValuesOps[Field, DeviceId] =
    new NeighborValuesOps[Field, DeviceId]:
      extension [A](a: Field[A])
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
        override def apply(id: DeviceId): A = a.neighborValues.getOrElse(id, a.defaultValue)
        private[xc] override def set(id: DeviceId, value: A): SharedData[A] = Field[A](
          a.default,
          a.neighborValues + (id -> value),
        )
        override def default: A = a.defaultValue
        override def values: Map[DeviceId, A] = a.neighborValues
        override def get(id: DeviceId): Option[A] = a.neighborValues.get(id)
      end extension

  override given sharedDataApplicative: Applicative[Field] = new Applicative[Field]:
    override def pure[A](x: A): Field[A] = Field(x, Map.empty)

    override def ap[A, B](ff: Field[A => B])(fa: Field[A]): Field[B] = Field(
      ff.defaultValue(fa.defaultValue),
      (ff.neighborValues.keySet ++ fa.neighborValues.keySet)
        .map(deviceId => deviceId -> ff(deviceId)(fa(deviceId)))
        .toMap,
    )

    override def map[A, B](fa: Field[A])(f: A => B): Field[B] = Field[B](
      f(fa.defaultValue),
      fa.neighborValues.view.mapValues(f).toMap,
    )

  override given sharedDataOps: SharedDataOps[Field] = new SharedDataOps[Field]:
    extension [A](field: Field[A])
      override def withoutSelf: SafeIterable[A] = SafeIterable(field.neighborValues.values)
      override def onlySelf: A = field(localId)

  override given convert[T]: Conversion[T, SharedData[T]] = Field[T](_)

  override def device: SharedData[DeviceId] = Field[DeviceId](localId, alignedDevices.map(id => (id, id)).toMap)
end FieldBasedSharedData
