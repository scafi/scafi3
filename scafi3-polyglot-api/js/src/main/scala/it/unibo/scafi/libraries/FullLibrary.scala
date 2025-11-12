package it.unibo.scafi.libraries

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.message.Codable
import it.unibo.scafi.message.JSCodable.jsAnyCodable
import it.unibo.scafi.types.{ EqWrapper, JSTypes }

/**
 * A JavaScript version of the full Scafi library.
 * @param lang
 *   the language context providing the necessary syntax and semantics for all the libraries.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
@JSExportTopLevel("FullLibrary")
class FullLibrary(using
    lang: AggregateFoundation & BranchingSyntax & FieldBasedSharedData & FieldCalculusSyntax & {
      type DeviceId = EqWrapper[js.Any]
    },
) extends FullPortableLibrary
    with JSFieldBasedSharedData
    with JSTypes:

  override given valueCodable[Value, Format]: Conversion[Value, Codable[Value, Format]] =
    jsAnyCodable.asInstanceOf[Conversion[Value, Codable[Value, Format]]]

  override given deviceIdConversion[ID]: Conversion[language.DeviceId, ID] = _.value.asInstanceOf[ID]
