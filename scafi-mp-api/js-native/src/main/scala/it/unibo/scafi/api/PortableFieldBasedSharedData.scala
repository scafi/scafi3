package it.unibo.scafi.api

/**
 * A portable definition of a field-based shared data structure, useful for non-jvm platforms.
 */
trait PortableFieldBasedSharedData extends PortableLibrary:
  ctx: PortableTypes & PortableExchangeCalculusLibrary =>
  import it.unibo.scafi.language.xc.FieldBasedSharedData

  override type Language <: AggregateFoundation & ExchangeSyntax & FieldBasedSharedData:
    type DeviceId = PortableDeviceId

  @JSExport("Field")
  @JSExportAll
  case class PortableField[Value](default: Value, neighborValues: Map[PortableDeviceId, Value])

  override type PortableSharedData[Value] = PortableField[Value]

  override given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] =
    Iso[PortableSharedData[T], language.SharedData[T]](pf =>
      val field = language.sharedDataApplicative.pure(pf.default)
      pf.neighborValues.foldLeft(field)((f, n) => f.set(n._1, n._2)),
    )(f => PortableField(f.default, f.neighborValues))

end PortableFieldBasedSharedData
