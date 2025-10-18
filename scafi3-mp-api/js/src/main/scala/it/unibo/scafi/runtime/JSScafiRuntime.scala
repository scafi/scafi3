package it.unibo.scafi.runtime

import scala.concurrent.ExecutionContext
import scala.scalajs.js

import it.unibo.scafi
import it.unibo.scafi.message.JSCodable
import it.unibo.scafi.message.JSCodable.given_Hash_Any
import it.unibo.scafi.types.{ EqWrapper, JSTypes }

import io.github.iltotore.iron.refineUnsafe

import scafi.context.xc.ExchangeAggregateContext
import scafi.message.UniversalCodable
import scafi.message.JSBinaryCodable.jsBinaryCodable
import scafi.runtime.bindings.{ ScafiEngineBinding, ScafiNetworkBinding }
import scafi.libraries.FullLibrary

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object JSScafiRuntime extends PortableRuntime with ScafiNetworkBinding with ScafiEngineBinding with JSTypes:

  given ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  trait JSAdts extends Adts:
    override type DeviceId = EqWrapper[js.Any]

    override given [ID] => Conversion[ID, DeviceId] = id => EqWrapper(id.asInstanceOf[js.Any])

    @JSExport @JSExportAll
    case class Endpoint(address: String, port: Int)

    override given toInetEndpoint: Conversion[Endpoint, scafi.runtime.network.sockets.InetTypes.Endpoint] = e =>
      scafi.runtime.network.sockets.InetTypes.Endpoint(e.address.refineUnsafe, e.port.refineUnsafe)

  trait JSRequirements extends Requirements with NoMemorySafeContext with JSAdts:
    override type AggregateLibrary = FullLibrary

    override given deviceIdCodable[Format]: UniversalCodable[DeviceId, Format] =
      new UniversalCodable[DeviceId, Format]:
        override def register(id: DeviceId): Unit = jsBinaryCodable.register(id.value)
        override def encode(id: DeviceId): Format = jsBinaryCodable.encode(id.value).asInstanceOf[Format]
        override def decode(data: Format): DeviceId = EqWrapper(jsBinaryCodable.decode(data.asInstanceOf[Array[Byte]]))

    override def library[ID](using Arena): ExchangeAggregateContext[ID] ?=> FullLibrary = FullLibrary()

  @JSExportTopLevel("Runtime")
  object JSAPI extends Api with NetworkBindings with EngineBindings with JSRequirements
end JSScafiRuntime
