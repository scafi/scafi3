package it.unibo.scafi.runtime.network

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

class JVMNativeTimeRetentionNetworkTest extends TimeRetentionNetworkTest:

  override given executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
