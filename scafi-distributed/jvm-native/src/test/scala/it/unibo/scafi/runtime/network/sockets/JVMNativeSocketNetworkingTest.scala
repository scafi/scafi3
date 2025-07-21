package it.unibo.scafi.runtime.network.sockets

import java.util.concurrent.ForkJoinPool

import scala.concurrent.ExecutionContext

import it.unibo.scafi.runtime.network.Codable.given

class JVMNativeSocketNetworkingTest extends SocketNetworkingBehavior:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(ForkJoinPool())

  given SocketConfiguration = SocketConfiguration.basic

  given PlainTextNetworking = new SocketNetworking:
    override type MessageIn = String
    override type MessageOut = String

  it should behave like anInboundConnectionListener

  it should behave like anOutboundConnection

  it should behave like both
