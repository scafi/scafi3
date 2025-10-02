package it.unibo.scafi.libraries

import scala.concurrent.ExecutionContext

import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.UniversalCodable
import it.unibo.scafi.presentation.JSBinaryCodable.jsBinaryCodable
import it.unibo.scafi.runtime.PortableRuntime
import it.unibo.scafi.runtime.bindings.{ ScafiEngineBinding, ScafiNetworkBinding }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object JSScafiRuntime extends PortableRuntime with ScafiNetworkBinding with ScafiEngineBinding with JSTypes:

  given ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  trait JSRequirements extends Requirements:
    type AggregateLibrary = FullLibrary

    override given [Value, Format]: UniversalCodable[Value, Format] =
      jsBinaryCodable.asInstanceOf[UniversalCodable[Value, Format]]

    override def library[ID]: ExchangeAggregateContext[ID] ?=> FullLibrary = FullLibrary()

  @JSExportTopLevel("Runtime")
  object JSAPI extends Api with NetworkBindings with EngineBindings with JSRequirements
