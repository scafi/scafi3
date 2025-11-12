package it.unibo.scafi.message

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array

import it.unibo.scafi.message.primitive.PrimitiveCodables.asPrimitiveCodable
import it.unibo.scafi.message.protobufjs.ProtobufJSType.fromProtobufJsMessage
import it.unibo.scafi.utils.JSUtils.{ asDynamic, hasFunctions, hasProps, typed }
import it.unibo.scafi.utils.Uint8ArrayOps.{ toByteArray, toUint8Array }

import cats.kernel.Hash

/**
 * A base trait for defining format- and library-agnostic binary codables for JavaScript objects.
 *
 * Any <strong>object</strong> passed to the library intended to be encoded or decoded in any format of choice is
 * expected to have a static members conforming this trait structure, i.e., providing coherent `encode` and `decode`
 * methods. Alternatively, the object can delegate the (de)serialization concerns to a property named `codable`.
 *
 * The following <strong>special cases</strong> apply:
 *   - For `number`, `string` and `boolean` primitives, a default codable is provided and it is possible to use them
 *     directly.
 *   - In <strong>JavaScript</strong>, if using `protobuf.js`, it is possible to use their <strong>generated
 *     messages</strong> types directly.
 *
 * <h1>Examples</h1>
 *
 * In the following examples, we show how it is possible to define valid codables for both JavaScript and Typescript.
 *
 * <h2>Plain JavaScript</h2>
 *
 * For JSON-based encoding/decoding:
 *
 * {{{
 * class Person {
 *     constructor(name, surname) {
 *         this.name = name;
 *         this.surname = surname;
 *     }
 *
 *     static encode(person) {
 *         return JSON.stringify({
 *             name: person.name,
 *             surname: person.surname
 *         });
 *     }
 *
 *     static decode(bytes) {
 *         return JSON.parse(bytes);
 *     }
 * }
 * }}}
 *
 * <h2>Typescript</h2>
 *
 * Using protobuf messages:
 *
 * {{{
 * class Foo implements HasCodable<Foo, Uint8Array> {
 *     private instance: ProtoFoo;
 *
 *     constructor(name: string, id: number) {
 *         this.instance = create(FooSchema, { name, id });
 *     }
 *
 *     get codable(): Codable<Foo, Uint8Array> {
 *         return Foo.codable;
 *     }
 *
 *     static codable: Codable<Foo, Uint8Array> = {
 *         encode: (foo) => toBinary(FooSchema, foo.instance),
 *         decode: (bytes) => {
 *             const decodedFoo = fromBinary(FooSchema, bytes);
 *             return new Foo(decodedFoo.name, decodedFoo.id);
 *         },
 *     };
 * }
 * }}}
 */
trait JSCodable extends js.Object:

  /**
   * Encodes the provided message into a well-known format.
   * @param message
   *   the message to encode
   * @return
   *   the encoded message
   */
  def encode(message: js.Any): js.Any

  /**
   * Decodes the provided data into a JavaScript object.
   * @param data
   *   the encoded data to decode
   * @return
   *   the decoded JavaScript object
   */
  def decode(data: js.Any): js.Any
end JSCodable

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object JSCodable:

  /**
   * Creates a `JSCodable` instance from the provided JavaScript value, if possible.
   * @param message
   *   the JavaScript value to convert into a `JSCodable`
   * @return
   *   a new `JSCodable` instance
   * @throws IllegalArgumentException
   *   if the provided value does not conform to the `JSCodable` structure.
   */
  def apply(message: js.Any): JSCodable = message.asPrimitiveCodable.getOrElse(apply(message.asInstanceOf[js.Object]))

  /**
   * Creates a `JSCodable` instance from the provided JavaScript object, if possible.
   * @param message
   *   the JavaScript object to convert into a `JSCodable`
   * @return
   *   a new `JSCodable` instance
   * @throws IllegalArgumentException
   *   if the provided object does not conform to the `JSCodable` structure.
   */
  def apply(message: js.Object): JSCodable = message.asJSCodable.getOrElse:
    throw new IllegalArgumentException("The provided value is not a valid `JSBinaryCodable`.")

  extension (message: js.Object)
    private def asJSCodable: Option[JSCodable] = message.fromProtobufJsMessage.orElse(message.asPlainJSCodable)

    private def asPlainJSCodable: Option[JSCodable] = message.asValidatedJSCodable.map(_.asInstanceOf[JSCodable])

    private def asValidatedJSCodable: Option[js.Dynamic] =
      def valid(Type: js.Dynamic): Option[js.Dynamic] =
        if !js.isUndefined(message) && Type.hasFunctions("encode", "decode") && Type.hasProps("typeName" typed "string")
        then Some(Type)
        else None
      valid(message.asDynamic.constructor).orElse(valid(message.asDynamic.codable))

  /** A `Hash` instance for `js.Any` values, which compares values by their encoded representation, if possible. */
  given codableHash: Hash[js.Any] = new Hash[js.Any]:
    override def hash(x: js.Any): Int = x.hashCode()
    override def eqv(x: js.Any, y: js.Any): Boolean = (JSCodable(x).encode(x), JSCodable(y).encode(y)) match
      case (encodedX: Uint8Array, encodedY: Uint8Array) => encodedX.toByteArray.sameElements(encodedY.toByteArray)
      case _ => throw new IllegalArgumentException("Cannot yet compare non-binary encoded values.")

  /**
   * A codable that can encode and decode in any JavaScript object that either corresponds to a primitive type (i.e.,
   * `string`, `number`, or `boolean`), or implements the [[JSCodable]] interface.
   */
  given jsAnyCodable: Conversion[js.Any, Codable[js.Any, Any]] = value =>
    new Codable[js.Any, Any]:
      private val codable = JSCodable(value)

      override def encode(value: js.Any): Any =
        codable.encode(value) match
          case bytes: Uint8Array => bytes.toByteArray
          case other => throw new IllegalArgumentException(s"$other (${js.typeOf(other)}) is not a supported format.")

      override def decode(data: Any): js.Any =
        data match
          case bytes: Array[Byte] => codable.decode(bytes.toUint8Array)
          case _ => throw new IllegalArgumentException(s"$data (${js.typeOf(data)}) is not a supported format.")
end JSCodable
