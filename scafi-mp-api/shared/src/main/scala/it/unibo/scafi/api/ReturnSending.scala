package it.unibo.scafi.api

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

@JSExportTopLevel("ReturnSending")
case class ReturnSending[+Value](returning: Value, sending: Value)

object ReturnSending:

  case class Continuation[Value](value: Value):
    @JSExport("send")
    def send(sending: Value): ReturnSending[Value] = ReturnSending(value, sending)

  @JSExportTopLevel("returning")
  def returning[Value](value: Value): Continuation[Value] = Continuation(value)

  @JSExportTopLevel("returnSending")
  def returnSending[Value](value: Value): ReturnSending[Value] = ReturnSending(value, value)
