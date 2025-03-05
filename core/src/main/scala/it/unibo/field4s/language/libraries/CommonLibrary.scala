package it.unibo.field4s.language.libraries

import it.unibo.field4s.language.foundation.{ AggregateFoundation, DeviceAwareAggregateFoundation }

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
  def self(using language: DeviceAwareAggregateFoundation): language.DeviceId = language.self

  /**
   * @return
   *   the aggregate value of device identifiers of aligned devices (including the current device)
   * @see
   *   [[DeviceAwareAggregateFoundation.device]]
   */
  def device(using
      language: AggregateFoundation & DeviceAwareAggregateFoundation,
  ): language.SharedData[language.DeviceId] = language.device
end CommonLibrary
