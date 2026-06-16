package it.unibo.scafi.message

import it.unibo.scafi.utils.InvocationCoordinate

trait Path extends IndexedSeq[InvocationCoordinate]

object Path:
  def apply(tokens: InvocationCoordinate*): Path = apply(tokens.toIndexedSeq)

  def apply(tokens: IndexedSeq[InvocationCoordinate]): Path = new Path:
    override def apply(i: Int): InvocationCoordinate = tokens(i)

    override def length: Int = tokens.length

    override def equals(o: Any): Boolean = o match
      case that: Path => this.toString == that.toString
      case _ => false

    override def hashCode: Int = tokens.hashCode()

    override def toString: String = tokens.mkString("Path(", ", ", ")")

  given CanEqual[Path, Path] = CanEqual.derived
  given CanEqual[Iterable[Path], Iterable[Path]] = CanEqual.derived
