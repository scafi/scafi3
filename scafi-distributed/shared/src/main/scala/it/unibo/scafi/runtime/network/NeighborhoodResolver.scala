package it.unibo.scafi.runtime.network

/** A set of neighbors' device identifiers. */
opaque type Neighborhood[DeviceId] = Set[DeviceId]

object Neighborhood:

  def apply[DeviceId](ids: Iterable[DeviceId]): Neighborhood[DeviceId] = Set.from(ids)

  def empty[DeviceId]: Neighborhood[DeviceId] = Set.empty[DeviceId]

/**
 * A neighborhood resolver, namely a function that in any given moment returns the current [[Neighborhood]].
 * @tparam DeviceId
 *   the type of the neighbors device identifiers.
 */
trait NeighborhoodResolver[DeviceId] extends (() => Neighborhood[DeviceId])
