package it.unibo.scafi.runtime.network.sockets

import it.unibo.scafi

import io.github.iltotore.iron.refineAllUnsafe
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scafi.runtime.network.sockets.InetTypes.{ Hostname, IPv4, IPv6 }

class InetTypesTest extends AnyFlatSpec with should.Matchers with Inspectors:

  inline private val inetTypesImport = "import scafi.runtime.network.sockets.InetTypes.*\n"

  it should "allow to define a valid port" in:
    inetTypesImport + "val port: Port = 8080" should compile
    inetTypesImport + "val port: Port = 0" should compile

  it should "forbid to define an invalid port" in:
    inetTypesImport + "val port: Port = 65_536" shouldNot compile
    inetTypesImport + "val port: Port = -1" shouldNot compile

  it should "allow to define valid IPv4 addresses" in:
    val validIPv4s = "192.168.1.1" :: "10.0.0.255" :: "172.16.254.3" :: "255.255.255.255" :: "0.0.0.0" :: Nil
    noException should be thrownBy (validIPv4s.refineAllUnsafe[IPv4])

  it should "forbid to define invalid IPv4 addresses" in:
    val invalidIPv4s = "256.100.100.100" :: "192.168.1" :: "192.168.01.1" :: "192.168.1.1.1" :: "192.168.1." :: Nil
    an[IllegalArgumentException] should be thrownBy invalidIPv4s.refineAllUnsafe[IPv4]

  it should "allow to define valid IPv6 addresses" in:
    val validIPv6s =
      "2001:0db8:85a3:0000:0000:8a2e:0370:7334" :: "2001:db8::1" :: "::1" :: "fe80::" :: "0:0:0:0:0:0:0:1" :: Nil
    noException should be thrownBy (validIPv6s.refineAllUnsafe[IPv6])

  it should "forbid to define invalid IPv6 addresses" in:
    val invalidIPv6s = "2001::85a3::8a2e" :: "2001:db8:85a3" :: "12345::abcd" :: "::1::" :: "fe80:::1" :: Nil
    an[IllegalArgumentException] should be thrownBy invalidIPv6s.refineAllUnsafe[IPv6]

  it should "allow to define valid hostnames" in:
    val validHostnames = "example.com" :: "localhost" :: "my-server.local" :: "google.com" :: Nil
    noException should be thrownBy (validHostnames.refineAllUnsafe[Hostname])

  it should "forbid to define invalid hostnames" in:
    val invalidHostnames = "example..com" :: "-invalid.com" :: "invalid" :: "test@gmail.com" :: Nil
    an[IllegalArgumentException] should be thrownBy invalidHostnames.refineAllUnsafe[Hostname]
end InetTypesTest
