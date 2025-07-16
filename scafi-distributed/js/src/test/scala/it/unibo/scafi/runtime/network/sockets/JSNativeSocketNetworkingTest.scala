package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

class JSNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  given SocketConfiguration = SocketConfiguration.basic

  override given executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  given Networking[Message, Message] = new SocketNetworking[String] {}

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
