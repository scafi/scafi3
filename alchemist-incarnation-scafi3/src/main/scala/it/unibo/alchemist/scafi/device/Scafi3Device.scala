package it.unibo.alchemist.scafi.device

import scala.math.Ordering.Implicits.infixOrderingOps

import it.unibo.alchemist.model.{ Environment, Node, NodeProperty, Position as AlchemistPosition, Time }
import it.unibo.alchemist.scafi.device.Scafi3Device.given
import it.unibo.scafi.message.Export
import it.unibo.scafi.runtime.network.{ ExpirationPolicy, LatestBufferingNetwork }

import org.apache.commons.math3.random.RandomGenerator

class Scafi3Device[T, Position <: AlchemistPosition[Position]](
    val random: RandomGenerator,
    val environment: Environment[T, Position],
    val node: Node[T],
    val retention: Time | Null,
) extends LatestBufferingNetwork
    with ExpirationPolicy
    with NodeProperty[T]:

  override type DeviceId = Int

  override type Metadata = Time

  override def current: Time = environment.getSimulation.getTime

  override lazy val localId: Int = node.getId

  override def send(message: Export[Int]): Unit =
    environment
      .getNeighborhood(node)
      .forEach: neighbor =>
        val scafiDevice = neighbor.asProperty(classOf[Scafi3Device[T, Position]])
        val messageForNeighbor = message(neighbor.getId)
        scafiDevice.deliverableReceived(localId, messageForNeighbor)

  extension (neighborMsg: (Int, Message))
    override def shouldBeDropped: Boolean = retention match
      case _: Time => neighborMsg._2.metadata.plus(retention) < current
      case null => false // scalafix:ok

  override def shouldCleanOnReceive: Boolean = retention == null // scalafix:ok

  override def getNode: Node[T] = node

  override def cloneOnNewNode(node: Node[T]): NodeProperty[T] =
    Scafi3Device(random, environment, node, retention)
end Scafi3Device

object Scafi3Device:
  given CanEqual[Time, Time] = CanEqual.derived
  given CanEqual[Null, Time | Null] = CanEqual.derived
  given CanEqual[Time | Null, Null] = CanEqual.derived
