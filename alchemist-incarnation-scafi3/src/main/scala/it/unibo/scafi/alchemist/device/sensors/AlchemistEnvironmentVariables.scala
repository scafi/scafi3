package it.unibo.scafi.alchemist.device.sensors

trait AlchemistEnvironmentVariables:
  def get[T](name: String): T

  def getOrNull[T](name: String): T | Null =
    try get[T](name)
    catch case _: NoSuchElementException => null

  def getOrDefault[T](name: String, default: T): T =
    try get[T](name)
    catch case _: NoSuchElementException => default

  def isDefined(name: String): Boolean

  def set[T](name: String, value: T): T
