package it.unibo.scafi.language.xc.calculus

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.NeighborValuesOps
import it.unibo.scafi.message.CodableFromTo

/**
 * This trait provides the semantics for the exchange calculus.
 */
trait ExchangeCalculus extends AggregateFoundation:

  /**
   * Operations on [[SharedData]] are provided as extension methods.
   *
   * @return
   *   an instance of [[SharedDataOps]]
   * @see
   *   [[NeighborValuesOps]]
   */
  given neighborValuesOps: NeighborValuesOps[SharedData, DeviceId] = scala.compiletime.deferred

  /**
   * Local values can be considered [[SharedData]].
   *
   * @tparam T
   *   can be any local value
   */
  given convert[T]: Conversion[T, SharedData[T]] = scala.compiletime.deferred

  /**
   * This single operator handles state and message reception/sending.
   *
   * @param init
   *   initial value for new devices
   * @param f
   *   function from neighbouring value to the couple (new local state, message to send)
   * @tparam Format
   *   the type of data format used to encode the local value to be distributed to neighbours
   * @tparam Value
   *   the type of neighbouring values
   * @return
   *   the neighbouring value providing for the next local state
   */
  protected def xc[Format, Value: CodableFromTo[Format]](init: SharedData[Value])(
      f: SharedData[Value] => (SharedData[Value], SharedData[Value]),
  ): SharedData[Value]
end ExchangeCalculus
