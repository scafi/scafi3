package it.unibo.scafi.runtime

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class JVMNativeDistributedScenarioTest extends DistributedScenarioTest:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  "Neighbors discovery program" should "behave like expected" in
    socketBasedDistributedEnvironment(neighborsDiscoveryProgram)

  "Exchange based aggregate program with branch restriction" should "behave like expected" in
    socketBasedDistributedEnvironment(exchangeWithRestrictionsProgram)
