package it.unibo.scafi.libraries

import it.unibo.scafi.language.xc.FieldBasedSharedData

/**
 * A portable definition of a field-based `SharedData` structure, useful for non-jvm platforms.
 */
trait PortableFieldBasedSharedData extends PortableLibrary:
  self: PortableTypes & PortableExchangeCalculusLibrary =>

  override type Language <: AggregateFoundation & ExchangeSyntax & FieldBasedSharedData

  override type SharedData[Value] = Field[Value]

  /**
   * A portable definition of a `SharedData` structure based on fields, isomorphic to the [[FieldBasedSharedData]] one.
   * @param default
   *   the default value for unaligned devices
   * @param neighborValues
   *   the values for all devices, aligned and unaligned
   */
  @JSExportAll
  case class Field[Value](default: Value, neighborValues: Map[language.DeviceId, Value])

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

  override given [T]: Iso[SharedData[T], language.SharedData[T]] =
    Iso[SharedData[T], language.SharedData[T]](pf =>
      val field = language.sharedDataApplicative.pure(pf.default)
      pf.neighborValues.foldLeft(field)((f, n) => f.set(n._1, n._2)),
    )(f => Field(f.default, f.neighborValues))

end PortableFieldBasedSharedData
