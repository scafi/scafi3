package it.unibo.scafi.runtime

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.libraries.{ FullPortableLibrary, PortableTypes }
import it.unibo.scafi.message.UniversalCodable
import it.unibo.scafi.runtime.network.sockets.ConnectionOrientedNetworkManager

/**
 * Portable runtime API entry point.
 */
trait PortableRuntime:
  self: PortableTypes =>

  /** The requirements that must be satisfied by any concrete implementation of the runtime. */
  trait Requirements:

    /** The universal codable instance used for encoding and decoding values to be sent over the network. */
    given [Value, Format]: UniversalCodable[Value, Format] = compiletime.deferred

    /** The aggregate library entry point. */
    type AggregateLibrary <: FullPortableLibrary[?]

    /** @return the concrete portable aggregate library instance. */
    def library[ID]: ExchangeAggregateContext[ID] ?=> AggregateLibrary

  trait Adts:

    /** A network endpoint consisting of an [[address]] and a [[port]]. */
    @JSExport @JSExportAll
    case class Endpoint(address: String, port: Int)

  /** The portable runtime API. */
  trait Api extends Adts:
    self: Requirements =>

    /** @return a socket-based network working on [[port]] with statically assigned [[neighbors]] and [[deviceId]]. */
    @JSExport
    def socketNetwork[ID](deviceId: ID, port: Int, neighbors: Map[ID, Endpoint]): ConnectionOrientedNetworkManager[ID]

    /**
     * Runs the given aggregate [[program]] on the device with the given [[deviceId]], using the provided [[network]].
     * The result of the program is passed to the [[onResult]] callback, which should return an [[Outcome]] indicating
     * whether the execution needs to continue or stop.
     */
    @JSExport
    def engine[ID, Result](
        deviceId: ID,
        network: ConnectionOrientedNetworkManager[ID],
        program: Function1[AggregateLibrary, Result],
        onResult: Function1[Result, Outcome[Boolean]],
    ): Outcome[Unit]
end PortableRuntime
