package it.unibo.scafi.engine.path

import scala.annotation.targetName

/**
  * A factory for creating paths.
  * 
  * @tparam A
  *  the type of the tokens used for the path
  * @tparam B
  *  the possibly transformed type of the tokens used for the path
  */
trait PathFactory[-A, +B]:
  /**
    * Creates a path from a sequence of tokens.
    *
    * @param sequence
    *  the sequence of tokens for building the path.
    * @return
    *  a path from the sequence of tokens according to the factory implementation.
    */
  def apply(sequence: Seq[A]): Path[B]