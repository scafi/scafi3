package it.unibo.scafi.libraries

/**
 * The portable library providing common utility functions that are often used in programs and libraries.
 */
trait PortableCommonLibrary extends PortableLibrary:
  ctx: PortableTypes =>

  /**
   * @return
   *   the device id of the current device
   */
  @JSExport
  def localId: language.DeviceId = language.localId

  /**
   * @return
   *   the aggregate value of device identifiers of aligned devices (including the current device)
   */
  @JSExport
  def device: SharedData[language.DeviceId] = language.device
