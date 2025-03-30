package it.unibo.scafi.engine.context.common

/**
 * Defines the basic operations to work with a type that wraps any objects, useful to define a collection of values with
 * different types.
 */
trait MessageManager:
  /**
   * The type of the envelope that wraps all the values.
   */
  type Envelope

  /**
   * Opens the envelope and returns the value inside it.
   * @param envelope
   *   the envelope
   * @tparam Payload
   *   the type of the value inside the envelope
   * @return
   *   the value inside the envelope
   */
  protected def open[Payload](envelope: Envelope): Payload

  /**
   * Closes the value inside the envelope.
   * @param a
   *   the value to wrap
   * @tparam Payload
   *   the type of the value to wrap
   * @return
   *   the envelope that wraps the value
   */
  protected def close[Payload](a: Payload): Envelope

end MessageManager

object MessageManager:

  /**
   * A basic implementation of the [[MessageManager]] trait that wraps values into [[Any]].
   */
  trait Basic extends MessageManager:
    override type Envelope = Any

    override protected def open[T](envelope: Any): T = envelope match
      case t: T @unchecked => t
      case _ => throw new ClassCastException(s"Cannot cast $envelope to requested type")

    override protected def close[T](payload: T): Any = payload
