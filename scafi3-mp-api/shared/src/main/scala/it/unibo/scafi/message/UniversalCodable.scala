package it.unibo.scafi.message

/**
 * A [[Codable]] that supports registering heterogeneous value types before encoding or decoding them. This trait
 * enables "universal" encoding and decoding capabilities: any value within the `Value` type bound becomes eligible for
 * encoding and decoding as long as its type has been registered. This is useful in dynamically typed environments or
 * scenarios where relying on static type and runtime reflection is not possible.
 * @tparam Value
 *   the upper bound of the values that can be registered, encoded, and decoded.
 * @tparam Format
 *   the type of the encoded format.
 */
trait UniversalCodable[Value, Format] extends Codable[Value, Format]:

  /**
   * Registers a value (or its type) so that values of the same type become eligible for encoding and decoding.
   * @param value
   *   the value to register.
   */
  def register(value: Value): Unit
