package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

class JSNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  override given executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
