package it.unibo.scafi.api

import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax

/**
 * The JVM entry point library, aggregating all the portable libraries.
 */
class XCLibrary(using lang: AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData)
    extends FullPortableLibrary
    with JVMTypes:
  import it.unibo.scafi.language.ShareDataOps

  override type PortableSharedData[Value] = language.SharedData[Value]

  override given [T]: Iso[PortableSharedData[T], language.SharedData[T]] = Iso.id

  given [T]: Conversion[T, PortableSharedData[T]] = language.convert

  given [T]: ShareDataOps[PortableSharedData, language.DeviceId] =
    new ShareDataOps[PortableSharedData, language.DeviceId]:
      import scala.collection.MapView
      extension [Value](sharedData: PortableSharedData[Value])
        override def default: Value = language.fieldOps.default(sharedData)
        override def values: MapView[language.DeviceId, Value] = language.fieldOps.values(sharedData)
        override def set(id: language.DeviceId, value: Value): PortableSharedData[Value] =
          language.fieldOps.set(sharedData)(id, value)
end XCLibrary
