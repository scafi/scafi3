package it.unibo.scafi.presentation

/**
 * A registry for JavaScript codables.
 *
 * This registry allows to register and retrieve codables by their identifier. The identifier is expected to be unique
 * across all registered codables.
 */
trait JSCodablesRegistry:

  /** The type used to identify codables in the registry. */
  type CodableId

  /**
   * Register a codable in the registry.
   * @param codable
   *   the codable to registers
   */
  def register(codable: JSCodable): JSCodablesRegistry & { type CodableId = JSCodablesRegistry.this.CodableId }

  /**
   * Retrieve a codable by its identifier.
   * @param codableId
   *   the identifier of the codable to retrieve
   * @return
   *   the codable associated with the provided identifier
   * @throws NotRegisteredCodableException
   *   if no codable is registered with the provided identifier
   */
  def apply(codableId: CodableId): JSCodable throws NotRegisteredCodableException

  /**
   * Safely retrieve a codable by its identifier.
   * @param id
   *   the identifier of the codable to retrieve
   * @return
   *   the codable associated with the provided identifier, wrapped in an `Some`, or `None` if no codable is registered
   */
  def get(id: CodableId): Option[JSCodable] =
    try Some(apply(id))
    catch case _: NotRegisteredCodableException => None

  /** Exception thrown when trying to retrieve a codable that is not registered. */
  case class NotRegisteredCodableException(id: CodableId) extends Exception(s"Type named $id is not registered.")
end JSCodablesRegistry

object JSCodablesRegistry:

  /** @return an empty registry for JS codables with string identifiers. */
  def forStringId(): JSCodablesRegistry & { type CodableId = String } = StringIdentifiedCodablesRegistry(Map.empty)

  /** @return a registry for JS codables with integer identifiers initialized with the given codables. */
  def forStringId(codables: Set[JSCodable]): JSCodablesRegistry & { type CodableId = String } =
    StringIdentifiedCodablesRegistry(codables.map(codable => codable.typeName -> codable).toMap)

  private class StringIdentifiedCodablesRegistry(mappings: Map[String, JSCodable]) extends JSCodablesRegistry:
    override type CodableId = String
    override def register(codable: JSCodable): JSCodablesRegistry & { type CodableId = String } =
      StringIdentifiedCodablesRegistry(mappings + (codable.typeName -> codable))
    override def apply(id: CodableId): JSCodable throws NotRegisteredCodableException =
      mappings.getOrElse(id, throw NotRegisteredCodableException(id))
