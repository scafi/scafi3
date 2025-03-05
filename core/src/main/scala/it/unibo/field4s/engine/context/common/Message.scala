package it.unibo.field4s.engine.context.common

/**
 * Defines the basic operations to work with a type that wraps any object, useful to define a collection of values with
 * different types.
 */
trait Message:
  /**
   * The type of the envelope that wraps all the values.
   */
  type Envelope

  /**
   * Opens the envelope and returns the value inside it.
   * @param a
   *   the envelope
   * @tparam T
   *   the type of the value inside the envelope
   * @return
   *   the value inside the envelope
   */
  protected def open[T](a: Envelope): T

  /**
   * Closes the value inside the envelope.
   * @param a
   *   the value to wrap
   * @tparam T
   *   the type of the value to wrap
   * @return
   *   the envelope that wraps the value
   */
  protected def close[T](a: T): Envelope

end Message

object Message:

  /**
   * A basic implementation of the [[Message]] trait that wraps values into [[Any]].
   */
  trait Basic extends Message:
    override type Envelope = Any

    override protected def open[T](a: Any): T = a match
      case t: T @unchecked => t
      case _ => throw new ClassCastException(s"Cannot cast $a to requested type")

    override protected def close[T](a: T): Any = a
