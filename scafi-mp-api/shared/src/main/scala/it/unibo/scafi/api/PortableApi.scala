package it.unibo.scafi.api

/**
 * Marker trait for portable APIs used to export platform utilities.
 */
trait PortableApi:
  export scala.scalajs.js.annotation.{ JSExport, JSExportAll }
