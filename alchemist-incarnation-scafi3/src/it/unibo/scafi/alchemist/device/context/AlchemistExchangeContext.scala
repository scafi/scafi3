package it.unibo.scafi.alchemist.device.context

import it.unibo.alchemist.model.{ Environment, Node, Position }
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.scafi.alchemist.device.sensors.AlchemistEnvironmentVariables
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.{ Import, ValueTree }
import it.unibo.scafi.message.Codables.forInMemoryCommunications

class AlchemistExchangeContext[T, P <: Position[P]](
    node: Node[T],
    environment: Environment[T, P],
    inbox: Import[Int],
    state: ValueTree,
) extends ExchangeAggregateContext[Int](node.getId, inbox, state),
      AbstractContextWithDistanceSensor,
      AlchemistEnvironmentVariables:

  override def senseDistance: Field[Double] =
    val devicePosition = environment.getPosition(node)
    neighborValues(devicePosition)(using forInMemoryCommunications).mapValues: (position: P) =>
      devicePosition.distanceTo(environment.makePosition(position.getCoordinates))

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  override def get[Value](name: String): Value = node.getConcentration(SimpleMolecule(name)).asInstanceOf[Value]

  override def isDefined(name: String): Boolean = node.contains(SimpleMolecule(name))

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  override def set[Value](name: String, value: Value): Value =
    node.setConcentration(SimpleMolecule(name), value.asInstanceOf[T])
    value

  override def deviceId: Int = node.getId
end AlchemistExchangeContext
