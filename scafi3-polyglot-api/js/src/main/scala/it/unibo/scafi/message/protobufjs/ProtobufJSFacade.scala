package it.unibo.scafi.message.protobufjs

import it.unibo.scafi.message.JSCodable
import it.unibo.scafi.utils.JSUtils.{ asDynamic, hasFunctions }

import scalajs.js
import scalajs.js.typedarray.Uint8Array

/**
 * A `protobuf.js` message type.
 * @see
 *   https://github.com/protobufjs/protobuf.js
 */
@js.native
trait ProtobufJSType extends js.Object:

  /**
   * Encodes the specified message. Does not implicitly verify messages.
   * @param message
   *   plain object to encode
   * @return
   *   the writer to encode to
   */
  def encode(message: js.Object): Writer = js.native

  /**
   * Decodes the specified binary data.
   * @param bytes
   *   the buffer to decode from
   * @return
   *   the decoded message
   * @throws Error
   *   if the payload is not a valid buffer or if the required fields are missing
   */
  def decode(bytes: Uint8Array): js.Object = js.native

  /**
   * Gets the default type url for Foo
   * @param typeUrlPrefix
   *   your custom prefix (default "type.googleapis.com")
   * @return
   *   the default type url for the message in the `typeUrlPrefix`/`messageType` format.
   */
  def getTypeUrl(typeUrlPrefix: String): String = js.native
end ProtobufJSType

/**
 * Wire format writer.
 */
@js.native
trait Writer extends js.Object:

  /**
   * Finishes the write operation.
   * @return
   *   the finished buffer
   */
  def finish(): Uint8Array = js.native

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object ProtobufJSType:

  extension (message: js.Object)
    /** @return the `JSCodable` instance obtained from the `protobuf.js` message, if applicable, or `None`. */
    inline def fromProtobufJsMessage: Option[JSCodable] = asValidatedProtobufJsType.map(_.asInstanceOf[ProtobufJSType])

    private def asValidatedProtobufJsType: Option[js.Dynamic] =
      val Type = message.asDynamic.constructor
      if !js.isUndefined(message) && Type.hasFunctions("encode", "decode", "getTypeUrl")
      then Some(Type)
      else None

  given Conversion[ProtobufJSType, JSCodable] = protobufJsType =>
    new JSCodable:
      override def encode(message: js.Any): js.Any = protobufJsType.encode(message.asInstanceOf[js.Object]).finish()
      override def decode(bytes: js.Any): js.Any = protobufJsType.decode(bytes.asInstanceOf[Uint8Array])
end ProtobufJSType
