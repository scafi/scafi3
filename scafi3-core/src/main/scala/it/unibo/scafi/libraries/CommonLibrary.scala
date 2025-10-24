package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation

/**
 * This library provides some common utility functions that are often used in programs and libraries.
 */
object CommonLibrary:
  /**
   * Returns one of the two values based on the condition. If the condition is true, the first value is returned,
   * otherwise the second value is returned. It's useful in place of if/branching when alignment between devices must be
   * preserved, thanks to eager evaluation.
   * @param cond
   *   the condition
   * @param th
   *   the value to return if the condition is true
   * @param el
   *   the value to return if the condition is false
   * @tparam T
   *   the type of the values
   * @return
   *   the value based on the condition
   */
  def mux[T](cond: Boolean)(th: T)(el: T): T = if cond then th else el

  /**
   * @return
   *   the device id of the current device
   * @see
   *   [[DeviceAwareAggregateFoundation.self]]
   */
  def localId(using language: AggregateFoundation): language.DeviceId = language.localId

  /**
   * @return
   *   the aggregate value of device identifiers of aligned devices (including the current device)
   * @see
   *   [[DeviceAwareAggregateFoundation.device]]
   */
  def device(using language: AggregateFoundation): language.SharedData[language.DeviceId] = language.device

  /**
   * Given a token, performs a manual alignment calling the body function..
   *
   * @param token
   *   to be used for alignment
   * @param body
   *   the code to align
   * @tparam T
   *   the type of the result
   * @return
   *   an aligned computation
   * @return
   */
  def align[T](token: Any)(body: () => T)(using aggregateFoundation: AggregateFoundation): T =
    aggregateFoundation.align(token)(body)
end CommonLibrary
