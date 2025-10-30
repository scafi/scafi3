package it.unibo.scafi.runtime

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.UniversalCodable
import it.unibo.scafi.runtime.network.sockets.InetTypes
import it.unibo.scafi.types.{ MemorySafeContext, PortableTypes }

/**
 * Portable runtime API entry point.
 */
trait PortableRuntime:
  self: PortableTypes =>

  trait Adts:

    /** The type used internally to the scafi core engine to identify devices. */
    type DeviceId

    /** Generic type can be turned into internal `DeviceId` representation and viceversa. */
    given [ID] => Conversion[ID, DeviceId] = compiletime.deferred

    /** The universal codable instance used for encoding and decoding device ids to be sent over the network. */
    given deviceIdCodable[Format]: UniversalCodable[DeviceId, Format] = compiletime.deferred

    /** A network endpoint consisting of an [[address]] and a [[port]]. */
    type Endpoint

    /** Endpoint is isomorphic to [[InetTypes.Endpoint]]. */
    given toInetEndpoint: Conversion[Endpoint, InetTypes.Endpoint] = compiletime.deferred

  /** The requirements that must be satisfied by any concrete implementation of the runtime. */
  trait Requirements extends MemorySafeContext:
    self: Adts =>

    /** The aggregate library entry point. */
    type AggregateLibrary

    /** @return the concrete portable aggregate library instance. */
    def library[ID](using Arena): ExchangeAggregateContext[ID] ?=> AggregateLibrary

  /** The portable runtime API. */
  trait Api:
    self: Requirements & Adts =>

    /**
     * Portable entry point for Scafi3 aggregate programs in a distributed environment with socket-based networking.
     * @param localId
     *   the unique identifier of the device
     * @param port
     *   the port on which the device listens for incoming messages
     * @param neighbors
     *   a map of neighboring device IDs to their network endpoints
     * @param program
     *   the aggregate program to run on the device
     * @param onResult
     *   a callback to handle the result of the program execution returning an Outcome of Boolean indicating whether to
     *   continue or stop
     */
    @JSExport
    def engine[ID, Result](
        localId: ID,
        port: Int,
        neighbors: Map[ID, Endpoint],
        program: Function1[AggregateLibrary, Result],
        onResult: Function1[Result, Outcome[Boolean]],
    ): Outcome[Unit]
  end Api
end PortableRuntime
