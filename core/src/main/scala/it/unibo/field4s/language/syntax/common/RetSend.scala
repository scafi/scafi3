package it.unibo.field4s.language.syntax.common

/**
 * Syntax sugar to return and send a value at the same time.
 * @param ret
 *   the value to return
 * @param send
 *   the value to send
 * @tparam T
 *   the type of the values
 */
case class RetSend[+T](ret: T, send: T)

object RetSend:
  given [T]: Conversion[(T, T), RetSend[T]] = RetSend(_, _)
  given [T]: Conversion[T, RetSend[T]] = RetSend(_)
  given [F[_], T](using Conversion[T, F[T]]): Conversion[T, RetSend[F[T]]] = RetSend(_)
  given [F[_], T](using Conversion[T, F[T]]): Conversion[RetSend[T], RetSend[F[T]]] = rs => RetSend(rs.ret, rs.send)

  /**
   * Syntax sugar to return and send a single value.
   * @param retsend
   *   the value to return and send
   * @tparam T
   *   the type of the value
   * @return
   *   a RetSend instance with the same value for ret and send
   */
  def apply[T](retsend: T): RetSend[T] = RetSend(retsend, retsend)

  /**
   * An object of this class enables the syntax to send a different value from the one to return.
   * @param ret
   *   the value to return
   * @tparam T
   *   the type of the value
   */
  case class Continuation[+T](ret: T):

    /**
     * The value to send.
     * @param send
     *   the value to send
     * @tparam T2
     *   the type of the value to send
     * @return
     *   a RetSend instance with the value to return and the value to send
     */
    inline infix def send[T2 >: T](send: T2): RetSend[T2] = RetSend(ret, send)

  /**
   * The value to return. It enables the syntax to send a different value from the one to return, using the send method
   * of the Continuation class.
   * @param ret
   *   the value to return
   * @tparam T
   *   the type of the value
   * @return
   *   an instance of the Continuation class, on which the send method can be called
   * @see
   *   [[Continuation]]
   */
  inline def ret[T](ret: T): Continuation[T] = Continuation(ret)

  /**
   * Alias for `RetSend(T)`.
   * @param retsend
   *   the value to return and send
   * @tparam T
   *   the type of the value
   * @return
   *   a RetSend instance with the same value for ret and send
   */
  inline def retsend[T](retsend: T): RetSend[T] = RetSend(retsend)
end RetSend
