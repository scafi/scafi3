package it.unibo.scafi.message

trait Path[+Token] extends IndexedSeq[Token]

object Path:
  def apply[Token](tokens: Token*): Path[Token] = new Path[Token]:
    override def apply(i: Int): Token = tokens(i)
    override def length: Int = tokens.length
