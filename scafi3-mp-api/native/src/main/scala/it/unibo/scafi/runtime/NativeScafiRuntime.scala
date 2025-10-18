package it.unibo.scafi.runtime

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.scalanative.unsafe.{ exported, fromCString, CInt, CVoidPtr, Ptr }

import it.unibo.scafi
import it.unibo.scafi.nativebindings.structs.BinaryCodable as CBinaryCodable

import io.github.iltotore.iron.refineUnsafe

import it.unibo.scafi.message.CBinaryCodable
import it.unibo.scafi.message.CBinaryCodable.given_Hash_Ptr
import it.unibo.scafi.types.EqWrapper
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.libraries.FullLibrary
import it.unibo.scafi.message.UniversalCodable
import it.unibo.scafi.message.NativeBinaryCodable.nativeBinaryCodable
import it.unibo.scafi.runtime.bindings.{ ScafiEngineBinding, ScafiNetworkBinding }
import it.unibo.scafi.runtime.network.sockets.ConnectionOrientedNetworkManager
import it.unibo.scafi.types.NativeTypes
import it.unibo.scafi.nativebindings.structs.Endpoint as CEndpoint
import it.unibo.scafi.runtime.network.sockets.InetTypes

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeScafiRuntime extends PortableRuntime with ScafiNetworkBinding with ScafiEngineBinding with NativeTypes:

  given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  trait NativeAdts extends Adts:
    override type DeviceId = EqWrapper[Ptr[CBinaryCodable]]

    override given [ID] => Conversion[ID, DeviceId] = id => EqWrapper(id.asInstanceOf[Ptr[CBinaryCodable]])

    override type Endpoint = Ptr[CEndpoint]

    override given toInetEndpoint: Conversion[Endpoint, InetTypes.Endpoint] = e =>
      InetTypes.Endpoint(fromCString((!e).address).refineUnsafe, (!e).port.refineUnsafe)

  trait NativeRequirements extends Requirements with NativeMemoryContext with NativeAdts:

    override type AggregateLibrary = Ptr[FullLibrary#CAggregateLibrary]

    override given deviceIdCodable[Format]: UniversalCodable[DeviceId, Format] =
      new UniversalCodable[DeviceId, Format]:
        override def register(id: DeviceId): Unit = nativeBinaryCodable.register(id.value)
        override def encode(id: DeviceId): Format = nativeBinaryCodable.encode(id.value).asInstanceOf[Format]
        override def decode(data: Format): DeviceId =
          EqWrapper(nativeBinaryCodable.decode(data.asInstanceOf[Array[Byte]]))

    override def library[ID](using Arena): ExchangeAggregateContext[ID] ?=> AggregateLibrary = FullLibrary().asNative

  object NativeApi extends Api with NativeAdts with NetworkBindings with EngineBindings with NativeRequirements:

    @exported("socket_network")
    def nativeSocketNetwork(
        deviceId: CVoidPtr,
        port: CInt,
        neighbors: Map[CVoidPtr, Endpoint],
    ): ConnectionOrientedNetworkManager[DeviceId] = socketNetwork[CVoidPtr](deviceId, port, neighbors)

    @exported("engine")
    def nativeEngine(
        network: ConnectionOrientedNetworkManager[DeviceId],
        program: Function1[AggregateLibrary, CVoidPtr],
        onResult: Function1[CVoidPtr, Outcome[Boolean]],
    ): Outcome[Unit] = engine(network, program, onResult)
end NativeScafiRuntime
