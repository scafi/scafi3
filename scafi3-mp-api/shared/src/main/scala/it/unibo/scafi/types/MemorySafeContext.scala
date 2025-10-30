package it.unibo.scafi.types

/**
 * A context allowing executing memory-safe operations within a scoped memory region.
 */
trait MemorySafeContext:

  /**
   * Represents a memory-safe allocation scope for the platform in use guaranteeing automatic memory management.
   */
  type Arena

  /**
   * Executes the given block safely within an `Arena` context.
   * @param block
   *   the code block to execute
   * @return
   *   the result of the block execution
   */
  def safelyRun[T](block: Arena ?=> T): T
