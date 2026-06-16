package it.unibo.scafi.utils

import scala.collection.mutable

import it.unibo.scafi.message.{ InvocationCoordinate, Path }

trait AlignmentManager:
  private val trace = mutable.Map.empty[Path, Int]
  private val stack = mutable.Stack.empty[InvocationCoordinate]

  /**
   * Returns the current path of the stack.
   * @return
   *   the current path of the stack.
   */
  protected def currentPath: Path = Path(stack.reverse.toIndexedSeq)

  /**
   * Executes the given function body inside a new scope, updating the current path accordingly. Restores the previous
   * path after the body has been executed.
   * @param key
   *   the key used to identify the scope.
   * @param body
   *   the function to be executed inside the scope.
   * @tparam Result
   *   the return type of the function.
   * @return
   *   the result of the function executed inside the scope.
   */
  protected def alignmentScope[Result](key: String)(body: () => Result): Result =
    val currentPath = Path(stack.toSeq*)
    val invocationCount = trace.get(currentPath).map(_ + 1).getOrElse(0)
    stack.push(InvocationCoordinate(key, invocationCount))
    try body()
    finally
      val _ = stack.pop()
      trace.update(currentPath, invocationCount)
end AlignmentManager
