package it.unibo.scafi.runtime

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class JVMDistributedScenarioTest extends DistributedScenarioTest:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  it should behave like expected()
