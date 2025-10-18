package it.unibo.scafi.message.primitive

import java.nio.charset.StandardCharsets.UTF_8

import it.unibo.scafi.message.JSCodable
import it.unibo.scafi.utils.Uint8ArrayOps.{ toByteArray, toUint8Array }

import scalajs.js
import scalajs.js.typedarray.Uint8Array

object PrimitiveCodables:

  private given stringCodable: JSCodable = deriveFromString("string", identity)

  private given numberCodable: JSCodable = deriveFromString("number", _.toDouble)

  private given booleanCodable: JSCodable = deriveFromString("boolean", _.toBoolean)

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
  private def deriveFromString(name: String, decodeStrategy: String => js.Any): JSCodable = new JSCodable:
    override def typeName: String = name
    override def encode(message: js.Any): js.Any = message.toString.getBytes(UTF_8).toUint8Array
    override def decode(data: js.Any): js.Any =
      decodeStrategy(new String(data.asInstanceOf[Uint8Array].toByteArray, UTF_8))

  /** @return the set of all codables for primitive JavaScript types. */
  def primitiveCodables: Set[JSCodable] = Set(stringCodable, numberCodable, booleanCodable)

  extension (message: js.Any)

    /**
     * Attempts to get the primitive codable corresponding to the type of the given message.
     * @return
     *   an optional [[JSCodable]] if the value corresponds to a primitive type, or `None` otherwise.
     */
    def asPrimitiveCodable: Option[JSCodable] = js.typeOf(message) match
      case t if t == "number" => Some(numberCodable)
      case t if t == "boolean" => Some(booleanCodable)
      case t if t == "string" => Some(stringCodable)
      case _ => None
end PrimitiveCodables
