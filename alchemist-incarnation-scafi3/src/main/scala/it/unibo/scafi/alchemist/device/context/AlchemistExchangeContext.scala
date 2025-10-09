package it.unibo.scafi.alchemist.device.context

import it.unibo.alchemist.model.{Environment, Node, Position}
import it.unibo.scafi.alchemist.device.sensors.AlchemistEnvironmentVariables
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.Codables.forInMemoryCommunications
import it.unibo.scafi.message.{Import, ValueTree}

class AlchemistExchangeContext[P <: Position[P]](
    node: Node[Any],
    environment: Environment[Any, P],
    inbox: Import[Int],
    state: ValueTree,
) extends ExchangeAggregateContext[Int](node.getId, inbox, state),
      AbstractContextWithDistanceSensor,
      AlchemistEnvironmentVariables:

  override def senseDistance: Field[Double] =
    val devicePosition = environment.getPosition(node)
    neighborValues(devicePosition)(using forInMemoryCommunications).mapValues: (position: P) =>
      devicePosition.distanceTo(environment.makePosition(position.getCoordinates))

  override def get[T](name: String): T = node.getConcentration(SimpleMolecule(name)).asInstanceOf[T]

  override def isDefined(name: String): Boolean = node.contains(SimpleMolecule(name))

  override def set[T](name: String, value: T): T =
    node.setConcentration(SimpleMolecule(name), value.asInstanceOf[Any])
    value

  override def deviceId: Int = node.getId
