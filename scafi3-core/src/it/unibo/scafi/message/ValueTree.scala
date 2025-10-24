package it.unibo.scafi.message

import it.unibo.scafi.message.ValueTree.NoPathFoundException

/**
 * For each aggregate operation, the association between a [[Path]] and the local [[Value]] is stored in this data
 * structure.
 *
 * This data structure is designed to hold heterogeneous values, so retrieving a value providing the wrong type will
 * result in a possible [[ClassCastException]]. Beware of this when using this data structure.
 */
trait ValueTree:
  /**
   * All the available [[Path]]s in the [[ValueTree]].
   * @return
   *   an iterable of all the available [[Path]]s in the [[ValueTree]].
   */
  def paths: Iterable[Path]

  /**
   * Retrieves the value associated to the given [[Path]].
   * @param path
   *   the path to retrieve the value for.
   * @tparam Value
   *   the type of the value to retrieve.
   * @return
   *   the value associated to the given [[Path]].
   */
  def apply[Value](path: Path): Value throws NoPathFoundException

  /**
   * Retrieves the value associated to the given [[Path]] if it exists, otherwise returns None.
   * @param path
   *   the path to retrieve the value for.
   * @tparam Value
   *   the type of the value to retrieve.
   * @return
   *   an Option containing the value associated to the given [[Path]] if it exists, otherwise None.
   */
  def get[Value](path: Path): Option[Value] =
    try Some(apply(path))
    catch case _: NoPathFoundException => None

  /**
   * Updates the [[value]] associated to the given [[Path]].
   * @param path
   *   the path to update the value for.
   * @param value
   *   the new value to associate to the given [[Path]].
   * @tparam Value
   *   the type of the value to update.
   * @return
   *   the updated [[ValueTree]].
   */
  def update[Value](path: Path, value: Value): ValueTree
end ValueTree

object ValueTree:
  /**
   * Exception thrown when a path is not found in the [[ValueTree]].
   *
   * @param path
   *   the path that was not found.
   */
  class NoPathFoundException(val path: Path) extends Exception

  /**
   * Creates a new [[ValueTree]] from the given [[underlying]] map of [[Path]]s and [[Value]]s.
   * @param from
   *   the map of [[Path]]s and [[Value]]s to create the [[ValueTree]] from.
   * @tparam Value
   *   the type of the value associated to the [[Path]].
   * @return
   *   a new [[ValueTree]] created from the given map of [[Path]]s and [[Value]]s.
   */
  def apply[Value](from: Map[Path, Value]): ValueTree = new ValueTree:
    override def paths: Iterable[Path] = from.keys
    @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
    override def apply[V](path: Path): V throws NoPathFoundException =
      from.get(path) match
        case Some(value) => value.asInstanceOf[V]
        case None => throw new NoPathFoundException(path)
    override def update[V](path: Path, value: V): ValueTree = ValueTree.apply(from + (path -> value))

    override def equals(obj: Any): Boolean =
      obj match
        case that: ValueTree =>
          try this.paths == that.paths && this.paths.forall(path => this(path) == that(path))
          catch case _: NoPathFoundException => false
        case _ => false

    override def hashCode(): Int = from.hashCode()

    override def toString: String =
      s"ValueTree(${from.map { case (k, v) => s"${k.mkString("/")} -> $v" }.mkString(", ")})"

  /**
   * Creates an empty [[ValueTree]].
   * @return
   *   an empty [[ValueTree]].
   */
  def empty: ValueTree = new ValueTree:
    override def paths: Iterable[Path] = Iterable.empty
    override def apply[V](path: Path): V throws NoPathFoundException = throw new NoPathFoundException(path)
    override def update[V](path: Path, value: V): ValueTree = ValueTree.apply(Map(path -> value))

    override def equals(obj: Any): Boolean =
      obj match
        case that: ValueTree =>
          this.paths.isEmpty && that.paths.isEmpty
        case _ => false

    override def hashCode(): Int = Iterable.empty.hashCode()

    override def toString: String = "ValueTree()"

  given valueTreeCanEqual: CanEqual[ValueTree, ValueTree] = CanEqual.derived
end ValueTree
