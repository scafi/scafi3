package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ForkJoinPool

import scala.concurrent.ExecutionContext

class JVMNativeSocketNetworkingTest extends NetworkingTest with SocketNetworkingBehavior:

  val networking = new SocketNetworking[String] {}

  override given ExecutionContext = ExecutionContext.fromExecutor(ForkJoinPool())

  it should behave like anInboundConnectionListener(networking)

  it should behave like anOutboundConnection(networking)

  it should behave like both(networking)
end JVMNativeSocketNetworkingTest
