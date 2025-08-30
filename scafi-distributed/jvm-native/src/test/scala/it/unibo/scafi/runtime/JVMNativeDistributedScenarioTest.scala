package it.unibo.scafi.runtime

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class JVMNativeDistributedScenarioTest extends DistributedScenarioTest:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
