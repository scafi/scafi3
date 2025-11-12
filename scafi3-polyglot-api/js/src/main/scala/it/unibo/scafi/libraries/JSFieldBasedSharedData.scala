package it.unibo.scafi.libraries

import scala.scalajs.js

import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.message.JSCodable
import it.unibo.scafi.message.JSCodable.codableHash
import it.unibo.scafi.types.{ EqWrapper, PortableTypes }

/**
 * A custom portable definition of a field-based `SharedData` structure for js platform.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait JSFieldBasedSharedData extends PortableLibrary:
  self: PortableTypes =>

  override type Language <: AggregateFoundation & FieldBasedSharedData & { type DeviceId = EqWrapper[js.Any] }

  override type SharedData[Value] = Field[Value]

  /**
   * A portable definition of a `SharedData` structure for js platform based on fields, isomorphic to the
   * [[FieldBasedSharedData]] one.
   * @param default
   *   the default value for unaligned devices
   * @param neighborValues
   *   the values for all devices, aligned and unaligned
   */
  @JSExportAll
  case class Field[Value](default: Value, neighborValues: Map[js.Any, Value]):

    @JSExport("withoutSelf")
    def jsWithoutSelf(): Seq[Value] = given_Iso_SharedData_SharedData.to(this).withoutSelf.toSeq

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
    def of[Value](default: Value): Field[Value] = Field(default, Map.empty)

  override given [Value]: Iso[SharedData[Value], language.SharedData[Value]] = Iso(
    jsField =>
      val field = language.sharedDataApplicative.pure(jsField.default)
      jsField.neighborValues.foldLeft(field)((f, n) => f.set(EqWrapper(n._1), n._2))
    ,
    scalaField =>
      val nvalues: Map[language.DeviceId, Value] = scalaField.neighborValues.map((id, v) => (deviceIdConversion(id), v))
      Field(scalaField.default, nvalues.asInstanceOf[Map[js.Any, Value]]),
  )

end JSFieldBasedSharedData
