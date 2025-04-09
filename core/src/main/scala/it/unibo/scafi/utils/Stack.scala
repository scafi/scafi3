package it.unibo.scafi.utils

trait Stack:
  
  protected def currentPath: IndexedSeq[Nothing]
  
  protected def scope[T](key: String)(body: () => T): T = ???
