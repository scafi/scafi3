package it.unibo.scafi.libraries

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.message.JSBinaryCodable.jsBinaryCodable
import it.unibo.scafi.types.{ EqWrapper, JSTypes }

/**
 * A JavaScript version of the full Scafi library.
 * @param lang
 *   the language context providing the necessary syntax and semantics for all the libraries.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
@JSExportTopLevel("FullLibrary")
class FullLibrary(using
    lang: AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData,
) extends FullPortableLibrary
    with JSFieldBasedSharedData
    with JSTypes:

  override given valueCodable[Value, Format]: UniversalCodable[Value, Format] =
    jsBinaryCodable.asInstanceOf[UniversalCodable[Value, Format]]

  override given deviceIdConv[ID]: Conversion[language.DeviceId, ID] =
    _.asInstanceOf[EqWrapper[js.Any]].value.asInstanceOf[ID]
