package it.unibo.scafi.api

object Api extends PortableXCApi:
  self =>

  object Interface extends self.Interface with ADTs with JVMTypes:
    import it.unibo.scafi.language.ShareDataOps

    override type PortableSharedData[Value] = Language#SharedData[Value]
    override given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] =
      Iso[PortableSharedData[T], language.SharedData[T]](data =>
        val localValue: language.SharedData[T] = data.default
        val neighbors = data.neighborValues
        neighbors.foldLeft(localValue)((f, n) => f.set(n._1, n._2)),
      )(identity)

    given [T](using language: Language): Conversion[T, PortableSharedData[T]] = language.convert

    given [T](using language: Language): ShareDataOps[PortableSharedData, PortableDeviceId] =
      new ShareDataOps[PortableSharedData, PortableDeviceId]:
        import scala.collection.MapView
        extension [Value](sharedData: PortableSharedData[Value])
          override def default: Value = language.fieldOps.default(sharedData)
          override def values: MapView[PortableDeviceId, Value] = language.fieldOps.values(sharedData)
          override def set(id: PortableDeviceId, value: Value): PortableSharedData[Value] =
            language.fieldOps.set(sharedData)(id, value)
  end Interface
end Api
