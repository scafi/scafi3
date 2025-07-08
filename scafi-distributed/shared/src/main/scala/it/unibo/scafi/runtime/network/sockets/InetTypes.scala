package it.unibo.scafi.runtime.network.sockets

// TODO: program with opaque types and safe constructors.
trait InetTypes:
  type Address = String

  type Port = Int

  type Endpoint = (Address, Port)

object InetTypes:

  val localhost = "localhost"
