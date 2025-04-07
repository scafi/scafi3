package it.unibo.scafi.testing

import it.unibo.scafi.engine.Engine
import it.unibo.scafi.engine.context.ContextFactory
import it.unibo.scafi.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.scafi.engine.network.{ Export, Import, Network }
import it.unibo.scafi.language.AggregateFoundation

trait Node[Value](using environment: Environment[Value]):
  def cycle(): Value

object Node:
  private class NodeImpl[Value](id: Int)(using Environment[Value]) extends Node[Value], Network[Int, Any]:
    private object Factory extends ContextFactory[NodeImpl[Value], NodeContext[Any]]:
      override def create(network: NodeImpl[Value]): NodeContext[Any] = ???

    private val engine = Engine(network = this, factory = Factory, program = ???)

    override def localId: Int = id
    override def send(e: Export[Int, Any]): Unit = ???
    override def receive(): Import[Int, Any] = ???
    override def cycle(): Value = ???

  private class NodeContext[Value](
      environment: Environment[Value],
      deviceId: Int,
      inbox: Import[Int, BasicExchangeCalculusContext.ExportValue],
  ) extends BasicExchangeCalculusContext[Int](deviceId, inbox)

  /**
   * .
   * @param environment
   * @tparam Value
   * @return
   */
  def apply[Value](using environment: Environment[Value]): Node[Value] = ???
end Node
