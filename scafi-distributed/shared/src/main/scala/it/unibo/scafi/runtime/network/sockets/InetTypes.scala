package it.unibo.scafi.runtime.network.sockets

import io.github.iltotore.iron.{ :|, DescribedAs }
import io.github.iltotore.iron.constraint.numeric.Interval
import io.github.iltotore.iron.constraint.string.Match

/**
 * Defines types for internet-based networking.
 */
object InetTypes:
  export io.github.iltotore.iron.{ assume, autoRefine }

  // IP addresses validation is performed using https://www.ditig.com/validating-ipv4-and-ipv6-addresses-with-regexp

  /** An IPv4 address. */
  type IPv4 = DescribedAs[Match["^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$"], "Must be a valid IPv4 address."]

  /** An IPv6 address. */
  type IPv6 = DescribedAs[
    Match[
      "^((?:[0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4}|(?:[0-9A-Fa-f]{1,4}:){1,7}:|:(?::[0-9A-Fa-f]{1,4}){1,7}|(?:[0-9A-Fa-f]{1,4}:){1,6}:[0-9A-Fa-f]{1,4}|(?:[0-9A-Fa-f]{1,4}:){1,5}(?::[0-9A-Fa-f]{1,4}){1,2}|(?:[0-9A-Fa-f]{1,4}:){1,4}(?::[0-9A-Fa-f]{1,4}){1,3}|(?:[0-9A-Fa-f]{1,4}:){1,3}(?::[0-9A-Fa-f]{1,4}){1,4}|(?:[0-9A-Fa-f]{1,4}:){1,2}(?::[0-9A-Fa-f]{1,4}){1,5}|[0-9A-Fa-f]{1,4}:(?:(?::[0-9A-Fa-f]{1,4}){1,6})|:(?:(?::[0-9A-Fa-f]{1,4}){1,6}))$",
    ],
    "Must be a valid IPv6 address.",
  ]

  /** A valid hostname. */
  type Hostname = DescribedAs[
    Match[
      "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$",
    ],
    "Must be a valid hostname.",
  ]

  /** A valid network address, either an IPv4, IPv6 address, or a valid hostname. */
  type Address = String :|
    DescribedAs[IPv4 | IPv6 | Hostname, "Must be a valid IP address, either IPv4 or IPv6, or a hostname."]

  /** A network port number. Valid values are in the range [0, 65,535). */
  type Port = Int :| Interval.ClosedOpen[0, 65_536]

  /**
   * A generic network endpoint.
   * @param address
   *   a valid network [[Address]].
   * @param port
   *   a valid [[Port]] number.
   */
  case class Endpoint(address: Address, port: Port)

  /** Localhost address. */
  val Localhost: Address = "localhost"

  /** The port used to instruct the operating system to assign a free ephemeral port. */
  val FreePort: Port = 0

  given CanEqual[Endpoint, Endpoint] = CanEqual.derived
end InetTypes
