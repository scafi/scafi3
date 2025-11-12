package it.unibo.scafi.libraries

import it.unibo.scafi.integration.JSTests
import it.unibo.scafi.integration.PlatformTest.ProgramOutput
import it.unibo.scafi.runtime.network.sockets.InetTypes.Port

import org.scalatest.flatspec.AnyFlatSpec

class JSApiTests extends CrossLanguageTests with JSTests:

  it should behave like neighborsDiscoveryTest()

  it should behave like domainRestrictionTest()

  it should behave like sensorExchangeTestWith("protobuf")

  override def neighborsAsCode(id: ID, neighbors: Set[ID], ports: Seq[Port]): ProgramOutput = neighbors
    .map(nid => s"[$nid, Runtime.Endpoint('localhost', ${ports(nid)})]")
    .mkString("[", ", ", "]")

  override def fieldRepr[Value](default: Value, neighbors: Map[ID, Value]): ProgramOutput =
    s"Field($default, $neighbors)"
