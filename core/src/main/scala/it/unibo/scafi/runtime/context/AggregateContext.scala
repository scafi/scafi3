package it.unibo.scafi.runtime.context

import it.unibo.scafi.runtime.network.OutboundMessage

trait AggregateContext[DeviceId]:
  def createOutboundMessage(): OutboundMessage
