package it.unibo.scafi.engine.context.common

/**
 * This class represents the coordinate of an invocation of a function or a branch. The index is useful when multiple
 * invocations are present at the same level of the call stack.
 * @param key
 *   the key of the function/branch
 * @param index
 *   the index of the invocation
 */
case class InvocationCoordinate(key: String, index: Int)
