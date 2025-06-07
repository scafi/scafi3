package it.unibo.scafi.api

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Api")
object JSXCApi extends PortableXCApi with PortableFieldBasedAggregateLibrary with JSTypes
