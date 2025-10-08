package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

/**
 * Connection oriented networking configuration.
 */
trait ConnectionConfiguration:

  /** The maximum acceptable size of a message in bytes that can be received over the socket stream. */
  val maxMessageSize: Int

  /** The maximum duration that a connection may remain open without receiving any messages. */
  val inactivityTimeout: FiniteDuration

object ConnectionConfiguration:

  /**
   * A basic configuration set up with a maximum message size of 65,536 bytes and an inactivity timeout of 60 seconds.
   */
  def basic: ConnectionConfiguration = new ConnectionConfiguration:
    override val maxMessageSize: Int = 65_536
    override val inactivityTimeout: FiniteDuration = 60.seconds

extension (duration: FiniteDuration)
  private[sockets] def toIntMillis: Int =
    val millisDuration = duration.toMillis
    if millisDuration > Int.MaxValue then Int.MaxValue else millisDuration.toInt
