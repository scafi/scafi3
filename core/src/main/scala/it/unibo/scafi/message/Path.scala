package it.unibo.scafi.message

trait Path[+Token] extends IndexedSeq[Token]

object Path:
  def apply[Token](tokens: Token*): Path[Token] = ???
