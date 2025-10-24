package it.unibo.scafi.message

trait Path extends IndexedSeq[Any]

object Path:
  def apply[Token](tokens: Token*): Path = new Path:
    override def apply(i: Int): Token = tokens(i)
    override def length: Int = tokens.length

    override def equals(o: Any): Boolean = o match
      case that: Path => this.toString == that.toString
      case _ => false

    override def hashCode: Int = tokens.hashCode()

    override def toString: String = tokens.mkString("Path(", ", ", ")")

  given CanEqual[Path, Path] = CanEqual.derived
  given CanEqual[Iterable[Path], Iterable[Path]] = CanEqual.derived
