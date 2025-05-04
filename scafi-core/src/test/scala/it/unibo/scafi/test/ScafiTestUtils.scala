package it.unibo.scafi.test

trait ScafiTestUtils:
  @SuppressWarnings(Array("DisableSyntax.asInstanceOf", "DisableSyntax.null"))
  def placeholder[T]: T = null.asInstanceOf[T]
