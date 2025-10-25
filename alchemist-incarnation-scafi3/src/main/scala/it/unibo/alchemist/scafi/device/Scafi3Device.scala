package it.unibo.alchemist.scafi.device

import scala.math.Ordering.Implicits.infixOrderingOps

import it.unibo.alchemist.model.{ Environment, Node, NodeProperty, Position as AlchemistPosition, Time }
import it.unibo.alchemist.scafi.device.Scafi3Device.given
import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager

import org.apache.commons.math3.random.RandomGenerator

class Scafi3Device[T, Position <: AlchemistPosition[Position]](
    val random: RandomGenerator,
    val environment: Environment[T, Position],
    val node: Node[T],
    val retention: Time | Null,
) extends NetworkManager,
      NodeProperty[T]:

  override type DeviceId = Int

  private case class TimedMessage(receivedAt: Time, payload: ValueTree)

  private var inbox: Map[Int, TimedMessage] = Map.empty

  private def time: Time = environment.getSimulation.getTime

  override lazy val localId: Int = node.getId

  override def send(message: Export[Int]): Unit =
    environment
      .getNeighborhood(node)
      .forEach: neighbor =>
        val scafiDevice = neighbor.asProperty(classOf[Scafi3Device[T, Position]])
        val messageForNeighbor = message(neighbor.getId)
        scafiDevice.deliverableReceived(localId, messageForNeighbor)

  override def receive: Import[Int] =
    retention match
      case _: Time =>
        inbox = inbox.filterNot { case (_, timedMessage) => timedMessage.receivedAt.plus(retention) < time }
        val messages = inbox.map { case (id, timedMessage) => id -> timedMessage.payload }
        Import(messages)

      case null => // scalafix:ok
        val messages = inbox.map { case (id, timedMessage) => id -> timedMessage.payload }
        inbox = Map.empty
        Import(messages)

  override def getNode: Node[T] = node

  override def cloneOnNewNode(node: Node[T]): NodeProperty[T] =
    Scafi3Device(random, environment, node, retention)

  override def deliverableReceived(from: Int, message: ValueTree): Unit =
    inbox += from -> TimedMessage(time, message)
end Scafi3Device

object Scafi3Device:
  given CanEqual[Time, Time] = CanEqual.derived
  given CanEqual[Null, Time | Null] = CanEqual.derived
