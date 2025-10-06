package it.unibo.scafi.alchemist.device

import it.unibo.alchemist.model.{ Environment, Node, NodeProperty, Time, Position as AlchemistPosition }
import it.unibo.scafi.message.{ Export, Import }
import it.unibo.scafi.runtime.network.NetworkManager
import org.apache.commons.math3.random.RandomGenerator

class ScaFiDevice[T, Position <: AlchemistPosition[Position]](
    val random: RandomGenerator,
    val env: Environment[T, Position],
    val node: Node[T],
    val retention: Time | Null,
) extends NetworkManager,
      NodeProperty[T]:

  override type DeviceId = Int

//  private var inbox: Map[Int, TimedMessage[ExportValue]] = Map.empty

  private def time: Time = ??? // env.getSimulation.nn.getTime.nn // TODO: maybe it should be a public var

//  override def send(e: Export[Int, ExportValue]): Unit = ???
//    inbox += localId -> TimedMessage(time, e(localId))
//    env
//      .getNeighborhood(node)
//      .nn
//      .forEach: n =>
//        val node: Node[T] = n.nn
//        node.asProperty(classOf[ScaFiDevice[T, Position, ExportValue]]).inbox += localId -> TimedMessage(
//          time, // TODO: current impl uses time of the sender
//          e(localId),
//        )

//  override def receive(): Import[Int, ExportValue] =
//    inbox = inbox.filterNot(_._2.time.plus(retention) < time)
//    inbox.map((id, timedMessage) => id -> timedMessage.message)

  override def send(message: Export[Int]): Unit = ???

  override def receive: Import[Int] = ???

  override def getNode: Node[T] = node

  override def cloneOnNewNode(node: Node[T]): NodeProperty[T] =
    ScaFiDevice(random, env, node, retention)
end ScaFiDevice
