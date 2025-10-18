package it.unibo.scafi.libraries

import scala.scalajs.js.annotation.{ JSExport, JSExportTopLevel }

/**
 * Syntax sugar to return and send a value at the same time.
 * @param returning
 *   the value to return
 * @param sending
 *   the value to send
 */
@JSExportTopLevel("ReturnSending")
case class ReturnSending[+Value](returning: Value, sending: Value)

object ReturnSending:

  /**
   * An object of this class enables the syntax to send a different value from the one to return.
   * @param returning
   *   the value to return
   */
  case class Continuation[+Value](returning: Value):

    /**
     * The value to send.
     * @param send
     *   the value to send
     * @return
     *   a [[ReturnSending]] instance with the value to return and the value to send
     */
    @JSExport
    infix def send[SendValue >: Value](send: SendValue): ReturnSending[SendValue] = ReturnSending(returning, send)

  /**
   * The value to return. It enables the syntax to send a different value from the one to return, using the `send`
   * method of the [[Continuation]] class.
   * @param value
   *   the value to return
   * @return
   *   an instance of the [[Continuation]] class, on which the `send` method can be called
   */
  @JSExportTopLevel("returning")
  @JSExport
  def returning[Value](value: Value): Continuation[Value] = Continuation(value)

  /**
   * Alias for `ReturnSending(value, value)`.
   * @param value
   *   the value to return and send
   * @return
   *   a [[ReturnSending]] instance with the same value for returning and sending
   */
  @JSExportTopLevel("returnSending")
  @JSExport
  def returnSending[Value](value: Value): ReturnSending[Value] = ReturnSending(value, value)

  @JSExportTopLevel("returnSending")
  @JSExport
  def returnSending[Value](returning: Value, sending: Value): ReturnSending[Value] = ReturnSending(returning, sending)
end ReturnSending
