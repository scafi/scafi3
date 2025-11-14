package it.unibo.scafi.runtime.network

import scala.concurrent.duration.{ Duration, MILLISECONDS, SECONDS }

/**
 * An expiration policy that drops received neighbor data after a certain time limit.
 * @param config
 *   the expiration configuration containing the time limit.
 */
trait TimeRetention(using config: ExpirationConfiguration) extends ExpirationPolicy:

  override type Metadata = Duration

  override def current: Metadata = Duration(System.currentTimeMillis(), MILLISECONDS)

  extension (neighborMsg: (DeviceId, Message))
    override def shouldBeDropped: Boolean = (current - neighborMsg._2.metadata) > config.limit

/** Configuration for expiration policies based on time retention. */
case class ExpirationConfiguration(limit: Duration)

object ExpirationConfiguration:

  /** A basic expiration configuration with a limit of 5 seconds. */
  val basic: ExpirationConfiguration = ExpirationConfiguration(Duration(5, SECONDS))
