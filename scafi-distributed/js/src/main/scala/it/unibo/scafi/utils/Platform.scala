package it.unibo.scafi.utils

import scala.concurrent.Promise
import scala.scalajs.js.timers.setTimeout

object Platform:

  def asyncOps: AsyncOperations = duration =>
    val p = Promise[Unit]()
    setTimeout(duration)(p.success(())): Unit
    p.future
