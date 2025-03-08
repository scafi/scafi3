package it.unibo.field4s.alchemist.device

import it.unibo.alchemist.model.{ Position as AlchemistPosition, * }
import it.unibo.field4s.engine.network.{ Export, Import, Network }
import it.unibo.field4s.alchemist.TimeUtils.given Ordering[Time]

import math.Ordering.Implicits.infixOrderingOps

class ScaFiDevice[T, Position <: AlchemistPosition[Position], ExportValue](
    val node: Node[T],
    val env: Environment[T, Position],
    val retention: Time,
) extends Network[Int, ExportValue]
    with NodeProperty[T]:
  private var inbox: Map[Int, TimedMessage[ExportValue]] = Map.empty

  private def time: Time = env.getSimulation.nn.getTime.nn // TODO: maybe it should be a public var

  override def localId: Int = node.getId

  override def send(e: Export[Int, ExportValue]): Unit =
    inbox += localId -> TimedMessage(time, e(localId))
    env
      .getNeighborhood(node)
      .nn
      .forEach: n =>
        val node: Node[T] = n.nn
        node.asProperty(classOf[ScaFiDevice[T, Position, ExportValue]]).inbox += localId -> TimedMessage(
          time, // TODO: current impl uses time of the sender
          e(localId),
        )

  override def receive(): Import[Int, ExportValue] =
    inbox = inbox.filterNot(_._2.time.plus(retention) < time)
    inbox.map((id, timedMessage) => id -> timedMessage.message)

  override def getNode: Node[T] = node

  override def cloneOnNewNode(node: Node[T]): NodeProperty[T] =
    ScaFiDevice(node, env, retention)
end ScaFiDevice
