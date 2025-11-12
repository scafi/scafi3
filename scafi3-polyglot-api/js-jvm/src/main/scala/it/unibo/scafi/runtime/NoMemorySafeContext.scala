package it.unibo.scafi.runtime
import it.unibo.scafi.types.MemorySafeContext

/**
 * A context where memory safety is enforced by construction by the underlying platform (e.g., JVM, JavaScript).
 */
trait NoMemorySafeContext extends MemorySafeContext:

  type Arena = Unit

  inline override def safelyRun[R](f: Arena ?=> R): R = f(using ())
