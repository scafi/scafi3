package it.unibo.scafi.runtime.network.sockets

trait InetTypes:
  type Address = String

  type Port = Int

  type Endpoint = (Address, Port)
