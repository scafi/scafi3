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

  given [A] => CanEqual[Field[A], Field[A]] = CanEqual.derived

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
  protected[scafi] case class Field[+Value](
      private[xc] val defaultValue: Value,
      private[xc] val devices: Set[DeviceId] = Set.empty,
      private[xc] val neighborValues: Map[DeviceId, Value] = Map.empty,
  ) extends SafeIterable[Value]:

    require(
      neighborValues.keySet.subsetOf(devices),
      "Neighbor values must be defined only for known devices\n" +
        s"Devices: $devices\n" +
        s"Neighbor values: ${neighborValues.keySet}",
    )

    /**
     * Two fields are equal if they have the same default and the same values for aligned devices or neighborValues not
     * in the other field contain the default value.
     * @param obj
     *   the other field
     * @return
     *   true if the fields are equal, false otherwise.
     */
    override def equals(obj: Any): Boolean =
      given CanEqual[Value, Value] = CanEqual.derived
      obj match
        case that: Field[Value @unchecked] =>
          this.defaultValue == that.defaultValue &&
          this.devices == that.devices &&
          this.neighborValues.filterNot(_._2 == this.defaultValue) == that.neighborValues.filterNot(
            _._2 == that.defaultValue,
          )
        case _ => false

    /**
     * @return
     *   a filtered view of the [[SharedData]] data that only contains the values for aligned devices
     */
    def alignedValues: Map[DeviceId, Value] =
      alignedDevices
        .map(id => id -> neighborValues.getOrElse(id, defaultValue))
        .toMap

    /**
     * @param id
     *   the device id
     * @return
     *   the value for the given device id, or the default value if the device is not aligned
     */
    def apply(id: DeviceId): Value = neighborValues.getOrElse(id, defaultValue)

    /**
     * Returns the value for the given device id if the device is aligned, None otherwise.
     * @param id
     *   the device id.
     * @return
     *   an Option containing the value for the given device id if aligned, None otherwise.
     */
    def get(id: DeviceId): Option[Value] =
      if alignedDevices.exists(_ == id) then Some(alignedValues.getOrElse(id, defaultValue))
      else None

    override def iterator: Iterator[Value] = alignedDevices
      .map(id => neighborValues.getOrElse(id, defaultValue))
      .iterator
    override def toString: String = s"Field($defaultValue, $devices, $neighborValues)"
  end Field

  /**
   * @return
   *   the set of device ids that are aligned with the current device
   */
  protected def alignedDevices: Iterable[DeviceId]

  override given neighborValuesOps: NeighborValuesOps[Field, DeviceId] =
    new NeighborValuesOps[Field, DeviceId]:
      extension [A](a: Field[A])
        override def apply(id: DeviceId): A = a.neighborValues.getOrElse(id, a.defaultValue)
        override def default: A = a.defaultValue
        override def values: Map[DeviceId, A] = a.neighborValues
        override def get(id: DeviceId): Option[A] = a.neighborValues.get(id)
        private[xc] override def set(id: DeviceId, value: A): Field[A] =
          given CanEqual[A, A] = CanEqual.derived
          if !a.devices.contains(id) then a
          else
            val newDevices = a.devices + id
            val newNeighborValues =
              if value == a.defaultValue then a.neighborValues - id
              else a.neighborValues + (id -> value)
            Field(a.defaultValue, newDevices, newNeighborValues)

  override given sharedDataApplicative: Applicative[Field] = new Applicative[Field]:
    override def pure[A](x: A): Field[A] = Field(x)
    override def ap[A, B](ff: Field[A => B])(fa: Field[A]): Field[B] =
      given [BB] => CanEqual[BB, BB] = CanEqual.derived
      val default = ff.defaultValue(fa.defaultValue)
      val allDevices = ff.devices ++ fa.devices
      val overrides = allDevices
        .map: id =>
          val transform = ff(id)
          val value = fa(id)
          val result = transform(value)
          id -> result
        .toMap
      Field(default, allDevices, overrides)

  override given sharedDataOps: SharedDataOps[Field] = new SharedDataOps[Field]:
    extension [A](field: Field[A])
      override def withoutSelf: SafeIterable[A] =
        given CanEqual[A, A] = CanEqual.derived
        SafeIterable(field.devices.toList.filterNot(_ == localId).map(field.apply))
      override def onlySelf: A = field(localId)

  override given convert[T]: Conversion[T, SharedData[T]] = Field[T](_)

  override def device: SharedData[DeviceId] =
    Field[DeviceId](localId, alignedDevices.toSet, alignedDevices.map(id => (id, id)).toMap)
end FieldBasedSharedData
