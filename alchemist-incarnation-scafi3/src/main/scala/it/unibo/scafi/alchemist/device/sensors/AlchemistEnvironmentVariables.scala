package it.unibo.scafi.alchemist.device.sensors

trait AlchemistEnvironmentVariables:
  
  def deviceId: Int
  
  def get[T](name: String): T

  def getOrNull[T](name: String): T | Null =
    try get[T](name)
    catch case _: NoSuchElementException => null

  def getOrDefault[T](name: String, default: T): T =
    try get[T](name)
    catch case _: NoSuchElementException => default

  def isDefined(name: String): Boolean

  def set[T](name: String, value: T): T

object AlchemistEnvironmentVariables:
  def deviceId(using env: AlchemistEnvironmentVariables): Int = env.deviceId
  
  def get[T](name: String)(using env: AlchemistEnvironmentVariables): T = env.get[T](name)

  def getOrNull[T](name: String)(using env: AlchemistEnvironmentVariables): T | Null =
    env.getOrNull[T](name)

  def getOrDefault[T](name: String, default: T)(using env: AlchemistEnvironmentVariables): T =
    env.getOrDefault[T](name, default)

  def isDefined(name: String)(using env: AlchemistEnvironmentVariables): Boolean = env.isDefined(name)

  def set[T](name: String, value: T)(using env: AlchemistEnvironmentVariables): T = env.set[T](name, value)
