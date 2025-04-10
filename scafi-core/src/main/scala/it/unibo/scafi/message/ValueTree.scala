package it.unibo.scafi.message

import language.experimental.saferExceptions

trait ValueTree:
  class NoPathFoundException(path: Path[?]) extends Exception

  def paths: Iterable[Path[?]]

  def apply[Value](path: Path[?]): Value throws NoPathFoundException

  def get[Value](path: Path[?]): Option[Value] =
    try Some(apply(path))
    catch case _: NoPathFoundException => None

  def update[Value](path: Path[?], value: Value): ValueTree

object ValueTree:
  def apply[TokenType, Value](underlying: Map[Path[TokenType], Value]): ValueTree = ???
  def empty[TokenType, Value]: ValueTree = ???
