package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ForkJoinPool

import scala.concurrent.ExecutionContext

class JVMNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(ForkJoinPool())

  given SocketConfiguration = SocketConfiguration.basic

  given Networking[Message, Message] = new SocketNetworking[String] {}

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
