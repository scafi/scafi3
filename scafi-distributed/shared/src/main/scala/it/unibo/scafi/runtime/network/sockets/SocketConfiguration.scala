package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

trait SocketConfiguration:

  val maxMessageSize: Int

  val connectionTimeout: FiniteDuration

object SocketConfiguration:

  def basic: SocketConfiguration = new SocketConfiguration:
    override val maxMessageSize: Int = 65_536 // 64 KB
    override val connectionTimeout: FiniteDuration = 30.seconds
