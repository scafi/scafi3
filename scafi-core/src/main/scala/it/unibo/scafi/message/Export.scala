package it.unibo.scafi.message

/**
 * Represents the produced [[ValueTree]]s for each [[DeviceId]].
 * @tparam DeviceId
 *   the type of the deviceId of neighbor devices.
 */
trait Export[DeviceId]:
  /**
   * Retrieves the [[ValueTree]] associated to the given [[deviceId]]. If the [[deviceId]] is not present in the
   * [[Export]], it returns the default [[ValueTree]].
   *
   * @param deviceId
   *   the id of the device to retrieve the [[ValueTree]] for.
   * @return
   *   the [[ValueTree]] associated to the given [[deviceId]] or the default [[ValueTree]] if the [[deviceId]] is not
   *   present.
   */
  def apply(deviceId: DeviceId): ValueTree

  /**
   * All the available [[DeviceId]]s in the [[Export]].
   * @return
   *   an iterable of all the available [[DeviceId]]s in the [[Export]].
   */
  def devices: Set[DeviceId]

object Export:
  /**
   * Creates an [[Export]] from a default [[ValueTree]] and a map of [[ValueTree]]s for each [[DeviceId]].
   * @param default
   *   the default [[ValueTree]] to use if the [[deviceId]] is not present in the map.
   * @param overrides
   *   the map of [[ValueTree]]s for each [[DeviceId]].
   * @tparam DeviceId
   *   the type of the deviceId of neighbor devices.
   * @return
   *   an [[Export]] that retrieves the [[ValueTree]] associated to the given [[deviceId]] or the default.
   */
  def apply[DeviceId](default: ValueTree, overrides: Map[DeviceId, ValueTree]): Export[DeviceId] = new Export[DeviceId]:
    override def apply(deviceId: DeviceId): ValueTree = overrides.getOrElse(deviceId, default)
    override def devices: Set[DeviceId] = overrides.keys.toSet
    given CanEqual[DeviceId, DeviceId] = CanEqual.derived

    @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
    override def equals(obj: Any): Boolean = obj match
      case that: Export[DeviceId] @unchecked =>
        this.devices == that.devices && this.devices.forall(deviceId => this(deviceId) == that(deviceId)) &&
        // Retrieve the default ValueTree using a non-existing DeviceId
        this.apply(Object().asInstanceOf[DeviceId]) == that.apply(Object().asInstanceOf[DeviceId])
      case _ => false

    override def hashCode(): Int = overrides.hashCode() + default.hashCode()

    override def toString: String =
      s"Export(${overrides.map { case (k, v) => s"$k -> $v" }.mkString(", ")}, default = $default)"

  given exportCanEqual[T]: CanEqual[Export[T], Export[T]] = CanEqual.derived
end Export
