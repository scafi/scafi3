package it.unibo.field4s.engine.path

/**
 * Alias for a list of tokens.
 * @tparam Token
 *   the type of the tokens
 */
type Path[Token] = List[Token]

object Path:

  /**
   * Creates an empty path.
   * @tparam A
   *   the type of the tokens
   * @return
   *   an empty path
   */
  def empty[A]: Path[A] = Nil

  /**
   * Creates a path from a collection of tokens.
   * @param coll
   *   the collection of tokens
   * @tparam B
   *   the type of the tokens
   * @return
   *   a path from the collection of tokens
   */
  def from[B](coll: collection.IterableOnce[B]): List[B] =
    Nil.prependedAll(coll)
end Path
