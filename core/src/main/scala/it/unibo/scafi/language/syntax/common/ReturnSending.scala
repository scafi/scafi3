package it.unibo.scafi.language.syntax.common

/**
 * Syntax sugar to return and send a value at the same time.
 * @param returning
 *   the value to return
 * @param sending
 *   the value to send
 * @tparam Value
 *   the type of the values
 */
case class ReturnSending[+Value](returning: Value, sending: Value)

object ReturnSending:
  given [Value]: Conversion[(Value, Value), ReturnSending[Value]] = ReturnSending(_, _)
  given [Value]: Conversion[Value, ReturnSending[Value]] = ReturnSending(_)
  given [F[_], Value](using Conversion[Value, F[Value]]): Conversion[Value, ReturnSending[F[Value]]] = ReturnSending(_)

  given [F[_], Value](using Conversion[Value, F[Value]]): Conversion[ReturnSending[Value], ReturnSending[F[Value]]] =
    rs => ReturnSending(rs.returning, rs.sending)

  /**
   * Syntax sugar to return and send a single value.
   * @param returnSending
   *   the value to return and send
   * @tparam Value
   *   the type of the value
   * @return
   *   a RetSend instance with the same value for ret and send
   */
  def apply[Value](returnSending: Value): ReturnSending[Value] = ReturnSending(returnSending, returnSending)

  /**
   * An object of this class enables the syntax to send a different value from the one to return.
   * @param returning
   *   the value to return
   * @tparam Value
   *   the type of the value
   */
  case class Continuation[+Value](returning: Value):

    /**
     * The value to send.
     * @param send
     *   the value to send
     * @tparam SendValue
     *   the type of the value to send
     * @return
     *   a RetSend instance with the value to return and the value to send
     */
    inline infix def send[SendValue >: Value](send: SendValue): ReturnSending[SendValue] =
      ReturnSending(returning, send)

  /**
   * The value to return. It enables the syntax to send a different value from the one to return, using the send method
   * of the Continuation class.
   * @param returning
   *   the value to return
   * @tparam Value
   *   the type of the value
   * @return
   *   an instance of the Continuation class, on which the send method can be called
   * @see
   *   [[Continuation]]
   */
  inline def returning[Value](returning: Value): Continuation[Value] = Continuation(returning)

  /**
   * Alias for `RetSend(T)`.
   * @param value
   *   the value to return and send
   * @tparam Value
   *   the type of the value
   * @return
   *   a RetSend instance with the same value for ret and send
   */
  inline def returnSending[Value](value: Value): ReturnSending[Value] = ReturnSending(value)
end ReturnSending
