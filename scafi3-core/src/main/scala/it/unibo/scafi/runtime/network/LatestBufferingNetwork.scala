package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ Import, ValueTree }

/**
 * A [[NetworkManager]] implementation that collects received messages in a buffer where only the most recent message
 * per neighbor is kept and where messages whose exceed a certain configurable lifetime may be dropped according to an
 * [[ExpirationPolicy]].
 */
trait LatestBufferingNetwork extends NetworkManager:
  self: ExpirationPolicy =>

  private var inbox = Map[DeviceId, Message]()

  override def receive: Import[DeviceId] = synchronized:
    val filteredInValues = inbox.filterNot(_.shouldBeDropped)
    inbox = if shouldCleanOnReceive then Map.empty else filteredInValues
    Import(filteredInValues.map(_ -> _.valueTree))

  override def deliverableReceived(from: DeviceId, message: ValueTree): Unit = synchronized:
    inbox += from -> Message(current, message)

  /**
   * Indicates whether the inbox should be cleaned after each receive operation.
   *
   * By default, this is set to false, meaning that the inbox is not automatically cleaned after each receive.
   * @return
   *   true if the inbox should be cleaned on receive, false otherwise.
   */
  protected def shouldCleanOnReceive: Boolean = false
end LatestBufferingNetwork

/**
 * Policy defining the received message expiration policy.
 */
trait ExpirationPolicy:

  /** The type of the neighbor device identifier. */
  type DeviceId

  /** Metadata associated with received neighbor message. */
  type Metadata

  /** Data structure to hold neighbor message along with its metadata. */
  protected case class Message(metadata: Metadata, valueTree: ValueTree)

  /** Provides the current metadata context for newly received messages. */
  protected def current: Metadata

  extension (neighborMsg: (DeviceId, Message))
    /**
     * Determines whether the neighbor data should be dropped based on its metadata.
     * @return
     *   true if the neighbor data should be dropped, false otherwise.
     */
    protected def shouldBeDropped: Boolean
end ExpirationPolicy
