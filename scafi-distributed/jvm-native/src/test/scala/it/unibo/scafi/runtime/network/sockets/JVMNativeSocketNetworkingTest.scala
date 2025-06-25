package it.unibo.scafi.runtime.network.sockets

class JVMNativeSocketNetworkingTest extends SocketNetworkingTest:

  import scala.concurrent.ExecutionContext

  override val networking: Networking[String, String] & InetTypes = new SocketNetworking[String] {}

  override given ExecutionContext = ExecutionContext.global

  tests()
