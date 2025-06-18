package it.unibo.scafi.api

/**
 * A portable definition of a field-based `SharedData` structure, useful for non-jvm platforms.
 */
trait PortableFieldBasedSharedData extends PortableLibrary:
  ctx: PortableTypes & PortableExchangeCalculusLibrary =>
  import it.unibo.scafi.language.xc.FieldBasedSharedData

  override type Language <: AggregateFoundation & ExchangeSyntax & FieldBasedSharedData

  @JSExportAll
  case class PortableField[Value](default: Value, neighborValues: Map[language.DeviceId, Value])

  @JSExport("Field")
  object PortableField:
    def of[Value](default: Value): PortableField[Value] = PortableField(default, Map.empty)

  override type SharedData[Value] = PortableField[Value]

  override given [T]: Iso[SharedData[T], language.SharedData[T]] =
    Iso[SharedData[T], language.SharedData[T]](pf =>
      val field = language.sharedDataApplicative.pure(pf.default)
      pf.neighborValues.foldLeft(field)((f, n) => f.set(n._1, n._2)),
    )(f => PortableField(f.default, f.neighborValues))

end PortableFieldBasedSharedData
