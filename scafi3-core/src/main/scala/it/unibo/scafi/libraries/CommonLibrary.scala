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
   * TOOD.
   *
   * @param path
   * @param body
   * @param aggregateFoundation
   * @tparam T
   * @return
   */
  def align[T](path: Any)(body: () => T)(using aggregateFoundation: AggregateFoundation): T =
    aggregateFoundation.align(path)(body)
end CommonLibrary
