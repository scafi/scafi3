package it.unibo.scafi.runtime

/**
 * A context where memory safety is enforced by construction by the underlying platform (e.g., JVM, JavaScript).
 */
trait NoMemorySafeContext extends MemorySafeContext:

  type Arena = Unit

  inline override def safelyRun[R](f: Arena ?=> R): R = f(using ())
