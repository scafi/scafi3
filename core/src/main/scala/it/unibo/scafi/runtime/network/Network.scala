package it.unibo.scafi.runtime.network

trait Network[DeviceId]:
  def send(message: OutboundMessage): Unit
  
  def processIncomingMessages(): InboundMessage