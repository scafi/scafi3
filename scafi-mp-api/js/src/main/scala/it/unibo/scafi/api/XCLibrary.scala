package it.unibo.scafi.api

import scala.scalajs.js.annotation.JSExportTopLevel

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.common.syntax.BranchingSyntax

@JSExportTopLevel("Api")
class XCLibrary(using
    AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData { type DeviceId = Int },
) extends PortableXCLibrary
    with PortableFieldBasedSharedData
    with JSTypes:

  override type Language = AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData:
    type DeviceId = Int

  override type PortableDeviceId = Int

  override given (using language: Language): Iso[PortableDeviceId, language.DeviceId] = Iso.id
