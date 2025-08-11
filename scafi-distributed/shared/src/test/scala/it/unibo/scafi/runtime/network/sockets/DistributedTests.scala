package it.unibo.scafi.runtime.network.sockets

import java.nio.charset.StandardCharsets

import scala.concurrent.ExecutionContext

import it.unibo.scafi.message.Codable
import it.unibo.scafi.runtime.network.sockets.InetTypes.*
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.context.xc.ExchangeAggregateContext.exchangeContextFactory
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.libraries.All.*

object DistributedTests:

  given ExecutionContext = ExecutionContext.global

  given SocketConfiguration = SocketConfiguration.basic

  given Codable[Int, Array[Byte]] = new Codable[Int, Array[Byte]]:
    inline def encode(msg: Int): Array[Byte] = msg.toString.getBytes(StandardCharsets.UTF_8)
    inline def decode(bytes: Array[Byte]): Int = new String(bytes, StandardCharsets.UTF_8).toInt

  type Lang = AggregateFoundation { type DeviceId = Int } & BranchingSyntax & ExchangeSyntax & FieldBasedSharedData

  def aggregateProgram(using Lang) =
    branch(localId % 2 == 0)(
      exchange(100)(returnSending),
    )(
      exchange(200)(returnSending),
    )

  @main def node0(): Unit =
    val deviceId = 0
    val port: Port = 8080
    val network = SocketBasedNetworkManager
      .withStaticallyAssignedNeighbors(
        deviceId,
        port,
        Map(
          1 -> Endpoint(Localhost, 8081),
          2 -> Endpoint(Localhost, 8082),
        ),
      )
    val engine = ScafiEngine(deviceId, network, exchangeContextFactory)(aggregateProgram)
    while true do
      val result = engine.cycle()
      println(s">>>> Node 1: $result")
      Thread.sleep(1_000)

  @main def node1(): Unit =
    val deviceId = 1
    val port: Port = 8081
    val network = SocketBasedNetworkManager
      .withStaticallyAssignedNeighbors(
        deviceId,
        port,
        Map(
          0 -> Endpoint(Localhost, 8080),
          3 -> Endpoint(Localhost, 8083),
        ),
      )
    val engine = ScafiEngine(deviceId, network, exchangeContextFactory)(aggregateProgram)
    while true do
      val result = engine.cycle()
      println(s">>>> Node 2: $result")
      Thread.sleep(1_000)

  @main def node2(): Unit =
    val deviceId = 2
    val port: Port = 8082
    val network = SocketBasedNetworkManager
      .withStaticallyAssignedNeighbors(
        deviceId,
        port,
        Map(
          0 -> Endpoint(Localhost, 8080),
          3 -> Endpoint(Localhost, 8083),
        ),
      )
    val engine = ScafiEngine(deviceId, network, exchangeContextFactory)(aggregateProgram)
    while true do
      val result = engine.cycle()
      println(s">>>> Node 3: $result")
      Thread.sleep(1_000)

  @main def node3(): Unit =
    val deviceId = 3
    val port: Port = 8083
    val network = SocketBasedNetworkManager
      .withStaticallyAssignedNeighbors(
        deviceId,
        port,
        Map(
          1 -> Endpoint(Localhost, 8081),
          2 -> Endpoint(Localhost, 8082),
        ),
      )
    val engine = ScafiEngine(deviceId, network, exchangeContextFactory)(aggregateProgram)
    while true do
      val result = engine.cycle()
      println(s">>>> Node 4: $result")
      Thread.sleep(1_000)
end DistributedTests
