package it.unibo.scafi.runtime

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.scalanative.unsafe.{ exported, fromCString, CInt, CVoidPtr, Ptr }

import it.unibo.scafi
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.libraries.FullLibrary
import it.unibo.scafi.message.Codable
import it.unibo.scafi.message.HashInstances.cBinaryCodableHash
import it.unibo.scafi.message.NativeCodable.nativeCodable
import it.unibo.scafi.nativebindings.structs.{
  AggregateLibrary as CAggregateLibrary,
  BinaryCodable as CBinaryCodable,
  Endpoint as CEndpoint,
}
import it.unibo.scafi.runtime.bindings.ScafiEngineBinding
import it.unibo.scafi.runtime.network.sockets.InetTypes
import it.unibo.scafi.types.{ EqWrapper, NativeTypes }

import io.github.iltotore.iron.refineUnsafe

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeScafiRuntime extends PortableRuntime with ScafiEngineBinding with NativeTypes:

  given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  trait NativeAdts extends Adts:
    override type DeviceId = EqWrapper[Ptr[CBinaryCodable]]

    override given [ID] => Conversion[ID, DeviceId] = id => EqWrapper(id.asInstanceOf[Ptr[CBinaryCodable]])

    override type Endpoint = Ptr[CEndpoint]

    override given toInetEndpoint: Conversion[Endpoint, InetTypes.Endpoint] = e =>
      InetTypes.Endpoint(fromCString((!e).address).refineUnsafe, (!e).port.refineUnsafe)

  trait NativeRequirements extends Requirements with NativeMemoryContext with NativeAdts:

    override type AggregateLibrary = Ptr[CAggregateLibrary]

    override given deviceIdCodable[Format]: Conversion[DeviceId, Codable[DeviceId, Format]] = id =>
      new Codable[DeviceId, Format]:
        private val codable = nativeCodable(id.value)
        override def encode(id: DeviceId): Format = codable.encode(id.value).asInstanceOf[Format]
        override def decode(data: Format): DeviceId = EqWrapper(codable.decode(data))

    override def library(using Arena): ExchangeAggregateContext[DeviceId] ?=> AggregateLibrary = FullLibrary().asNative

  object NativeApi extends Api with NativeAdts with EngineBindings with NativeRequirements:

    @exported("engine")
    def nativeEngine(
        deviceId: CVoidPtr,
        port: CInt,
        neighbors: Map[CVoidPtr, Endpoint],
        program: Function1[AggregateLibrary, CVoidPtr],
        onResult: Function1[CVoidPtr, Outcome[Boolean]],
    ): Outcome[Unit] = engine(deviceId, port, neighbors, program, onResult)
end NativeScafiRuntime
