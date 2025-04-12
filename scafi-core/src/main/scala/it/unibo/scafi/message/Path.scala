package it.unibo.scafi.message

trait Path extends IndexedSeq[Any]

object Path:
  def apply[Token](tokens: Token*): Path = new Path:
    override def apply(i: Int): Token = tokens(i)
    override def length: Int = tokens.length

  given CanEqual[Path, Path] = CanEqual.derived
  given CanEqual[Iterable[Path], Iterable[Path]] = CanEqual.derived
