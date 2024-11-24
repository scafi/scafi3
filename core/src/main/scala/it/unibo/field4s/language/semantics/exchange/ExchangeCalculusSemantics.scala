package it.unibo.field4s.language.semantics.exchange

import it.unibo.field4s.language.foundation.{ AggregateFoundation, DeviceAwareAggregateFoundation }
import it.unibo.field4s.language.semantics.exchange
import it.unibo.field4s.language.semantics.exchange.syntaxes.{
  BranchingExchangeSemantics,
  ExchangeSemantics,
  FieldCalculusByExchangeSemantics,
}

/**
 * This trait provides the semantics for the exchange calculus.
 */
trait ExchangeCalculusSemantics
    extends AggregateFoundation
    with DeviceAwareAggregateFoundation
    with ExchangeSemantics
    with BranchingExchangeSemantics
    with FieldCalculusByExchangeSemantics:

  /**
   * Operations on NValues are provided by the ExchangeCalculusSemantics as extension methods.
   * @return
   *   an instance of NValuesOps
   * @see
   *   [[NValuesOps]]
   */
  given nvalues: NValuesOps[AggregateValue, DeviceId]

  /**
   * Local values can be considered NValues.
   *
   * @tparam T
   *   can be any local value
   */
  given convert[T]: Conversion[T, AggregateValue[T]]

  /**
   * This operator branches the computation into `th` or `el` according to `cond`.
   */
  protected def br[T](cond: Boolean)(th: => T)(el: => T): T

  /**
   * This single operator handles state and message reception/sending.
   *
   * @param init
   *   initial value for new devices
   * @param f
   *   function from neighbouring value to the couple (new local state, message to send)
   * @tparam T
   *   the type of neighbouring values
   * @return
   *   the neighbouring value providing for the next local state
   */
  protected def xc[T](init: AggregateValue[T])(
      f: AggregateValue[T] => (AggregateValue[T], AggregateValue[T]),
  ): AggregateValue[T]
end ExchangeCalculusSemantics
