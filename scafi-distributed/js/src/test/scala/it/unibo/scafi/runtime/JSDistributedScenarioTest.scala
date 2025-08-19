package it.unibo.scafi.runtime

import scala.concurrent.ExecutionContext

class JSDistributedScenarioTest extends DistributedScenarioTest:

  override given executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  "Neighbors discovery program" should "behave like expected" in
    socketBasedDistributedEnvironment(neighborsDiscoveryProgram)

  "Exchange based aggregate program" should "behave like expected" in
    socketBasedDistributedEnvironment(exchangeWithRestrictionsProgram)
