package it.unibo.scafi.libraries

import scala.scalajs.js

import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.message.JSCodable
import it.unibo.scafi.message.JSCodable.given_Hash_Any
import it.unibo.scafi.types.{ EqWrapper, PortableTypes }

/**
 * A custom portable definition of a field-based `SharedData` structure for js platform.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait JSFieldBasedSharedData extends PortableLibrary:
  self: PortableTypes & PortableExchangeCalculusLibrary =>

  override type Language <: AggregateFoundation & ExchangeSyntax & FieldBasedSharedData

  override type SharedData[Value] = Field[js.Any, Value]

  /**
   * A portable definition of a `SharedData` structure for js platform based on fields, isomorphic to the
   * [[FieldBasedSharedData]] one.
   * @param default
   *   the default value for unaligned devices
   * @param neighborValues
   *   the values for all devices, aligned and unaligned
   */
  @JSExportAll
  case class Field[ID, Value](default: Value, neighborValues: Map[ID, Value]):
    valueCodable.register(default)
    override def toString(): String = s"Field($default, ${neighborValues.toMap.toSeq.sortBy(_._1.toString).toMap})"

  @JSExport
  @JSExportAll
  object Field:
    /**
     * @param default
     *   the default value for the field
     * @return
     *   a new [[Field]] instance with the given default value and no neighbor values.
     */
    def of[Value](default: Value): Field[js.Any, Value] = Field(default, Map.empty)

  override given [Value]: Iso[SharedData[Value], language.SharedData[Value]] = Iso(
    jsField =>
      val field = language.sharedDataApplicative.pure(jsField.default)
      jsField.neighborValues.foldLeft(field)((f, n) => f.set(EqWrapper(n._1).asInstanceOf[language.DeviceId], n._2))
    ,
    scalaField =>
      val nvalues: Map[language.DeviceId, Value] = scalaField.neighborValues.map((id, v) => (deviceIdConv(id), v)).toMap
      Field(scalaField.default, nvalues.asInstanceOf[Map[js.Any, Value]]),
  )

end JSFieldBasedSharedData
