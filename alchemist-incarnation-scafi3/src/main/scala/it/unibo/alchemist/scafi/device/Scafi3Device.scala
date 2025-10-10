package it.unibo.alchemist.scafi.device

import it.unibo.alchemist.model.{ Environment, Node, NodeProperty, Time, Position as AlchemistPosition }
import it.unibo.alchemist.scafi.device.Scafi3Device.given
import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.network.NetworkManager
import org.apache.commons.math3.random.RandomGenerator

import scala.math.Ordering.Implicits.infixOrderingOps

class Scafi3Device[Position <: AlchemistPosition[Position]](
    val random: RandomGenerator,
    val environment: Environment[Any, Position],
    val node: Node[Any],
    val retention: Time | Null,
) extends NetworkManager,
      NodeProperty[Any]:

  override type DeviceId = Int

  private case class TimedMessage(receivedAt: Time, payload: ValueTree)

  private var inbox: Map[Int, TimedMessage] = Map.empty

  private def time: Time = environment.getSimulation.getTime

  lazy val localId: Int = node.getId

  override def send(message: Export[Int]): Unit =
    environment
      .getNeighborhood(node)
      .forEach: neighbor =>
        val scafiDevice = neighbor.asProperty(classOf[Scafi3Device[Position]])
        val messageForNeighbor = message(neighbor.getId)
        scafiDevice.deliverableReceived(localId, messageForNeighbor)

  override def receive: Import[Int] =
    retention match
      case _: Time =>
        inbox = inbox.filterNot { case (_, timedMessage) => timedMessage.receivedAt.plus(retention) < time }
        val messages = inbox.map { case (id, timedMessage) => id -> timedMessage.payload }
        Import(messages)

      case null =>
        val messages = inbox.map { case (id, timedMessage) => id -> timedMessage.payload }
        inbox = Map.empty
        Import(messages)

  override def getNode: Node[Any] = node

  override def cloneOnNewNode(node: Node[Any]): NodeProperty[Any] =
    Scafi3Device(random, environment, node, retention)

  override def deliverableReceived(from: Int, message: ValueTree): Unit =
    inbox += from -> TimedMessage(time, message)
end Scafi3Device

object Scafi3Device:
  given CanEqual[Time, Time] = CanEqual.derived
  given CanEqual[Null, Time | Null] = CanEqual.derived
