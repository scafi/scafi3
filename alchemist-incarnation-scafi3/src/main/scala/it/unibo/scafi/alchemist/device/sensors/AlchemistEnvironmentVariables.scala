package it.unibo.scafi.alchemist.device.sensors

/**
 * Provides utility methods to interact with the Alchemist [[it.unibo.alchemist.model.Environment]].
 * The interface allows to get and set the value of molecules, as well as to check if a molecule is defined.
 * It also provides a method to get the identifier of the current device.
 *
 * {{{
 *   type Lang = AggregateFoundation { type DeviceId = Int } &
 *      FieldCalculusSyntax & DistanceSensor[Double] &
 *      AlchemistEnvironmentVariables
 *
 *   def program(using Lang) =
 *    val id = AlchemistEnvironmentVariables.deviceId
 *    val isSource = AlchemistEnvironmentVariables.getOrElse[Boolean]("source", false)
 *    val threshold = AlchemistEnvironmentVariables.get[Double]("threshold")
 *    AlchemistEnvironmentVariables.set("lastId", id)
 * }}}
 */
trait AlchemistEnvironmentVariables:

  def deviceId: Int

  def get[T](name: String): T

  def getOption[T](name: String): Option[T] =
    try Some(get[T](name))
    catch case _: NoSuchElementException => None

  def getOrElse[T](name: String, default: T): T =
    getOption[T](name).getOrElse(default)

  def isDefined(name: String): Boolean

  def set[T](name: String, value: T): T

object AlchemistEnvironmentVariables:
  /**
   * Returns the identifier of the current device.
   * @param env
   *   the environment from which to get the device id.
   * @return
   *   the device id.
   */
  def deviceId(using env: AlchemistEnvironmentVariables): Int = env.deviceId

  /**
   * Return the value associated to the given molecule name.
   * @param name
   *   the name of the molecule to retrieve.
   * @param env
   *   the environment from which to get the molecule.
   * @tparam T
   *   the type of the value associated to the molecule.
   * @return
   *   the value associated to the molecule.
   * @throws NoSuchElementException
   *   if the molecule is not defined.
   */
  def get[T](name: String)(using env: AlchemistEnvironmentVariables): T = env.get[T](name)

  /**
   * Return the value associated to the given molecule name, wrapped in an Option.
   * @param name
   *   the name of the molecule to retrieve.
   * @param env
   *   the environment from which to get the molecule.
   * @tparam T
   *   the type of the value associated to the molecule.
   * @return
   *   [[Some]](value) if the molecule is defined, [[None]] otherwise.
   */
  def getOption[T](name: String)(using env: AlchemistEnvironmentVariables): Option[T] =
    env.getOption[T](name)

  /**
   * Return the value associated to the given molecule name, or a default value if the molecule is not defined.
   * @param name
   *   the name of the molecule to retrieve.
   * @param default
   *   the default value to return if the molecule is not defined.
   * @param env
   *   the environment from which to get the molecule.
   * @tparam T
   *   the type of the value associated to the molecule.
   * @return
   *   the value associated to the molecule, or the default value if the molecule is not defined.
   */
  def getOrElse[T](name: String, default: T)(using env: AlchemistEnvironmentVariables): T =
    env.getOrElse[T](name, default)

  /**
   * Check if a molecule with the given name is defined.
   * @param name
   *   the name of the molecule to check.
   * @param env
   *   the environment from which to check the molecule.
   * @return
   *   true if the molecule is defined, false otherwise.
   */
  def isDefined(name: String)(using env: AlchemistEnvironmentVariables): Boolean = env.isDefined(name)

  /**
   * Set the value associated to the given molecule name.
   * @param name
   *   the name of the molecule to set.
   * @param value
   *   the value to set.
   * @param env
   *   the environment in which to set the molecule.
   * @tparam T
   *   the type of the value to set.
   * @return
   *   the value that was set.
   */
  def set[T](name: String, value: T)(using env: AlchemistEnvironmentVariables): T = env.set[T](name, value)
end AlchemistEnvironmentVariables
