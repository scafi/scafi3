package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

class JSNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  given SocketConfiguration = SocketConfiguration.basic

  override given executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  val networking = new SocketNetworking[String] {}

  it should behave like anInboundConnectionListener(networking)

  it should behave like anOutboundConnection(networking)

  it should behave like both(networking)
