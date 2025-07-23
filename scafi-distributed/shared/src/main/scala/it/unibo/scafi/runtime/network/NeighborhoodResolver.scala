package it.unibo.scafi.runtime.network

/**
 * A set of neighbors' device identifiers.
 */
type Neighborhood[DeviceId] = Set[DeviceId]

object Neighborhood:

  def apply[DeviceId](ids: Iterable[DeviceId]): Neighborhood[DeviceId] = Set.from(ids)

  def empty[DeviceId]: Neighborhood[DeviceId] = Set.empty[DeviceId]

/**
 * A neighborhood resolver is responsible for resolving all the neighbors of a device based on some criteria.
 */
trait NeighborhoodResolver:

  /** The type of device identifiers. */
  type DeviceId

  /**
   * Resolves the neighborhood of the calling device.
   * @return
   *   the neighborhood of the device.
   */
  def resolve(): Neighborhood[DeviceId]
