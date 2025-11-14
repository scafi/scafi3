package it.unibo.scafi.runtime.network

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

class JSTimeRetentionNetworkTest extends TimeRetentionNetworkTest:

  override given executionContext: ExecutionContext = JSExecutionContext.Implicits.queue
