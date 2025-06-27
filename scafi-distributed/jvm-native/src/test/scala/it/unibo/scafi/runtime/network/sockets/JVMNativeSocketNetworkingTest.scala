package it.unibo.scafi.runtime.network.sockets

class JVMNativeSocketNetworkingTest extends NetworkingTest with SocketNetworkingBehavior:

  import scala.concurrent.ExecutionContext

  val networking = new SocketNetworking[String] {}

  override given ExecutionContext = ExecutionContext.global

  it should behave like anInboundConnectionListener(networking)

  it should behave like anOutboundConnection(networking)
