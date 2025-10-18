package it.unibo.scafi.message

import it.unibo.scafi.message.CodablesRegistry

trait JSCodablesRegistry extends CodablesRegistry[JSCodablesRegistry]:
  override type Codable = JSCodable

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
