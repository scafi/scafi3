package it.unibo.field4s.engine.context.common

import scala.collection.mutable

import it.unibo.field4s.engine.path.Path

/**
 * Implements the semantics of tracing the location of function invocation with a stack and provides a current path for
 * aligning with inbound value trees.
 */
trait Stack:
  this: MessageManager =>

  private val trace: mutable.Map[Path[InvocationCoordinate], Int] =
    mutable.Map.empty[Path[InvocationCoordinate], Int]
  private val stack: mutable.Stack[InvocationCoordinate] = mutable.Stack.empty

  /**
   * @return
   *   the current path of invocation coordinates, that is the path of the location of the current function scope.
   */
  protected def currentPath: IndexedSeq[InvocationCoordinate] = stack.reverse.toIndexedSeq

  /**
   * Executes the given function body inside a new scope, updating the current path accordingly. Restores the previous
   * path after the body has been executed.
   * @param key
   *   the key of the new scope
   * @param body
   *   the function body to execute
   * @tparam T
   *   the return type of the function body
   * @return
   *   the result of the function body
   */
  protected def scope[T](key: String)(body: () => T): T =
    val next = trace.get(stack.toList).map(_ + 1).getOrElse(0)
    stack.push(InvocationCoordinate(key, next))
    val result = body()
    val _ = stack.pop()
    val _ = trace.put(stack.toList, next)
    result
end Stack
