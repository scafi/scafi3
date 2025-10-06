package it.unibo.scafi.alchemist.device.context

import it.unibo.alchemist.model.{Environment, Node, Position}
import it.unibo.scafi.context.xc.ExchangeAggregateContext
import it.unibo.scafi.message.Codables.forInMemoryCommunications
import it.unibo.scafi.message.{Import, ValueTree}

class AlchemistExchangeContext[P <: Position[P]](
    node: Node[Any],
    environment: Environment[Any, P],
    inbox: Import[Int],
    state: ValueTree,
) extends ExchangeAggregateContext[Int](node.getId, inbox, state),
      AbstractContextWithDistanceSensor:

  override def senseDistance: Field[Double] =
    val devicePosition = environment.getPosition(node)
    neighborValues(devicePosition)(using forInMemoryCommunications).mapValues: (position: P) =>
      devicePosition.distanceTo(environment.makePosition(position.getCoordinates))
