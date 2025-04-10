package it.unibo.scafi.runtime.network

import it.unibo.scafi.message.{ Export, Import }

trait Network[DeviceId]:
  def send(message: Export[DeviceId]): Unit
  def receive: Import[DeviceId]
