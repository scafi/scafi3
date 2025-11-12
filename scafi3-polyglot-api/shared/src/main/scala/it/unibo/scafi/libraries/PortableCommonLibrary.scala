package it.unibo.scafi.libraries
import it.unibo.scafi.types.PortableTypes

/**
 * The portable library providing common utility functions that are often used in programs and libraries.
 */
trait PortableCommonLibrary extends PortableLibrary:
  self: PortableTypes =>

  /**
   * @return
   *   the device id of the current device
   */
  @JSExport
  def localId[ID]: ID = language.localId
