package it.unibo.scafi.api

trait PortableFieldBasedAggregateLibrary extends PortableCommonLibrary:
  ctx: PortableTypes =>
  import it.unibo.scafi.language.xc.FieldBasedSharedData

  override type Language <: AggregateFoundation & FieldBasedSharedData { type DeviceId = PortableDeviceId }

  @JSExport("Field")
  @JSExportAll
  case class PortableField[Value](default: Value, neighborValues: Map[PortableDeviceId, Value])

  override type PortableSharedData[Value] = PortableField[Value]

  override given [Value](using language: Language): Iso[PortableSharedData[Value], language.SharedData[Value]] =
    // TODO: improve it adding a factory to create a `language.SharedData[Value]` out of default and neighbor values?
    Iso[PortableSharedData[Value], language.SharedData[Value]](pf =>
      val field: language.SharedData[Value] = pf.default
      val neighborsMap: collection.Map[PortableDeviceId, Value] = pf.neighborValues
      neighborsMap.foldLeft(field)((f, n) => f.set(n._1, n._2)),
    )(f => PortableField(f.default, f.values.toMap))
end PortableFieldBasedAggregateLibrary
