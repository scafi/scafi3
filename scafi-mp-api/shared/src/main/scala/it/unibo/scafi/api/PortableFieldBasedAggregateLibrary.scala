package it.unibo.scafi.api

trait PortableFieldBasedAggregateLibrary extends PortableCommonLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language.xc.FieldBasedSharedData

  override type Language <: AggregateFoundation & FieldBasedSharedData { type DeviceId = PortableDeviceId }

  override type PortableSharedData[Value] = PortableField[Value]

  @JSExport("Field")
  @JSExportAll
  case class PortableField[Value](default: Value, neighborValues: Map[PortableDeviceId, Value])

  override given [Value](using language: Language): Iso[PortableSharedData[Value], language.SharedData[Value]] =
    Iso[PortableSharedData[Value], language.SharedData[Value]](pf =>
      val field: language.SharedData[Value] = pf.default
      pf.neighborValues.foldLeft(field)((f, n) => f.set(n._1, n._2)),
    )(f => PortableField(f.default, f.values.toMap))
end PortableFieldBasedAggregateLibrary
