package it.unibo.scafi.api

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Api")
object JSXCApi extends PortableXCApi:

  @JSExport("Interface")
  object JSInterface extends Interface with ADTs with JSTypes with PortableFieldBasedAggregateLibrary
