package it.unibo.scafi.runtime

import scala.concurrent.ExecutionContext

class JSDistributedScenarioTest extends DistributedScenarioTest:

  override given executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
