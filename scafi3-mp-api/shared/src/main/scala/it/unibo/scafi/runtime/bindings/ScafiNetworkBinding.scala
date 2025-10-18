package it.unibo.scafi.runtime.bindings

import scala.concurrent.ExecutionContext

import it.unibo.scafi
import it.unibo.scafi.types.PortableTypes

import io.github.iltotore.iron.refineUnsafe

import scafi.runtime.PortableRuntime
import scafi.runtime.network.sockets.{ ConnectionConfiguration, ConnectionOrientedNetworkManager, SocketNetworkManager }

/**
 * Provides a concrete implementation of the portable runtime API for the ScaFi network.
 */
trait ScafiNetworkBinding extends PortableRuntime:
  self: PortableTypes =>

  trait NetworkBindings(using ExecutionContext) extends Api:
    self: Requirements & Adts =>

    override def socketNetwork[ID](
        deviceId: ID,
        port: Int,
        neighbors: Map[ID, Endpoint],
    ): ConnectionOrientedNetworkManager[DeviceId] =
      deviceIdCodable.register(deviceId)
      val net = neighbors.view.map((id, e) => (id: DeviceId, toInetEndpoint(e))).toMap
      SocketNetworkManager.withFixedNeighbors(deviceId, port.refineUnsafe, net)

    given ConnectionConfiguration = ConnectionConfiguration.basic
