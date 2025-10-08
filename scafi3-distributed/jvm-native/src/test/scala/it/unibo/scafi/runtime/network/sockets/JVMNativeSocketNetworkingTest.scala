package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class JVMNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
