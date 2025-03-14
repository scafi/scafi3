package it.unibo.field4s.alchemist

import scala.jdk.CollectionConverters.*

import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.{ Environment, Node, Position as AlchemistPosition }
import it.unibo.field4s.engine.context.exchange.BasicExchangeCalculusContext
import it.unibo.field4s.engine.network.Import
import it.unibo.field4s.language.exchange.ExchangeLanguage
import it.unibo.field4s.language.sensors.DistanceSensor

class AlchemistContext[T, Position <: AlchemistPosition[Position]](
    environment: Environment[T, Position],
    deviceId: Int,
    inbox: Import[Int, AlchemistContext.ExportValue],
) extends BasicExchangeCalculusContext[Int](deviceId, inbox)
    with DistanceSensor[Double]
    with AlchemistActuators
    with AlchemistSensors
    with ExchangeLanguage:

  private def me: Node[T] = environment.getNodeByID(deviceId).nn

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
  override def sense[Value](name: String): Value =
    environment.getNodeByID(deviceId).nn.getConcentration(SimpleMolecule(name)).asInstanceOf[Value]

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  override def update[Value](name: String, value: Value): Unit =
    environment.getNodeByID(deviceId).nn.setConcentration(SimpleMolecule(name), value.asInstanceOf[T])

  override def senseDistance: SharedData[Double] =
    val myPosition = environment.getPosition(me).nn
    val distances = environment
      .getNeighborhood(me)
      .nn
      .asScala
      .map(n =>
        n.getId ->
          environment.getPosition(n).nn.distanceTo(myPosition),
      )
      .toMap
    Field(Double.PositiveInfinity, distances + (deviceId -> 0.0))
end AlchemistContext

object AlchemistContext:
  type ExportValue = BasicExchangeCalculusContext.ExportValue

  def sense[Value](sensor: String)(using alchemist: AlchemistContext[?, ?]): Value = alchemist.sense(sensor)

  def update[Value](actuator: String, value: Value)(using alchemist: AlchemistContext[?, ?]): Unit =
    alchemist.update(actuator, value)
