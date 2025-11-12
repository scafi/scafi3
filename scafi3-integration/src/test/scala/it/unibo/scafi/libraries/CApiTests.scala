package it.unibo.scafi.libraries

import it.unibo.scafi.integration.CTests
import it.unibo.scafi.integration.PlatformTest.ProgramOutput
import it.unibo.scafi.runtime.network.sockets.InetTypes.Port

import org.scalatest.flatspec.AnyFlatSpec

class CApiTests extends CrossLanguageTests with CTests:

  it should behave like neighborsDiscoveryTest()

  it should behave like domainRestrictionTest()

  it should behave like sensorExchangeTestWith("protobuf")

  override def neighborsAsCode(id: ID, neighbors: Set[ID], ports: Seq[Port]): ProgramOutput = neighbors
    .map(nid => s"""
      |struct Endpoint device${nid}_endpoint = { address("localhost"), ${ports(nid)} };
      |Neighborhood_put(neighbors, device($nid), &device${nid}_endpoint);
      |""".stripMargin)
    .mkString(sep = "\n")

  override def fieldRepr[Value](default: Value, neighbors: Map[ID, Value]): ProgramOutput =
    s"Field($default, ${neighbors.mkString("[", ", ", "]")})"
