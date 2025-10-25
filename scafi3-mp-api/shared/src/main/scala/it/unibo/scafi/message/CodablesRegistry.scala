package it.unibo.scafi.message

/**
 * A registry for codables.
 *
 * This registry allows to register and retrieve codables by their identifier. The identifier is expected to be unique
 * across all registered codables.
 */
trait CodablesRegistry[Self <: CodablesRegistry[Self]]:
  self: Self =>

  /** The concrete type of codables managed by this registry. */
  type Codable

  /** The type used to identify codables in the registry. */
  type CodableId

  /**
   * Register a codable in the registry.
   * @param codable
   *   the codable to register
   */
  def register(codable: Codable): Self & { type CodableId = self.CodableId }

  /**
   * Retrieve a codable by its identifier.
   * @param codableId
   *   the identifier of the codable to retrieve
   * @return
   *   the codable associated with the provided identifier
   * @throws NotRegisteredCodableException
   *   if no codable is registered with the provided identifier
   */
  def apply(codableId: CodableId): Codable throws NotRegisteredCodableException

  /**
   * Safely retrieve a codable by its identifier.
   * @param id
   *   the identifier of the codable to retrieve
   * @return
   *   the codable associated with the provided identifier, wrapped in an `Some`, or `None` if no codable is registered
   */
  def get(id: CodableId): Option[Codable] =
    try Some(apply(id))
    catch case _: NotRegisteredCodableException => None

  /** Exception thrown when trying to retrieve a codable that is not registered. */
  case class NotRegisteredCodableException(id: CodableId) extends Exception(s"Type named $id is not registered.")
end CodablesRegistry
