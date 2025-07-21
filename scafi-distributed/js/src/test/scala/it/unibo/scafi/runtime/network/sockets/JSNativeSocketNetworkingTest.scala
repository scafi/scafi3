package it.unibo.scafi.runtime.network.sockets

import scala.concurrent.ExecutionContext

import it.unibo.scafi.runtime.network.Codable.given

class JSNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  given SocketConfiguration = SocketConfiguration.basic

  override given executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  given Networking[Message, Message] = new SocketNetworking[Message] {}

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
