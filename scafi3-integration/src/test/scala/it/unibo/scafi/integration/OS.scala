package it.unibo.scafi.integration

trait OS:

  /** @return true if the operating system is Windows, false otherwise. */
  def isWindows: Boolean = system.contains("windows")

  /** @return true if the operating system is MacOS, false otherwise. */
  def isMac: Boolean = system.contains("mac")

  /** @return true if the operating system is Linux, false otherwise. */
  def system: String = System.getProperty("os.name").toLowerCase
