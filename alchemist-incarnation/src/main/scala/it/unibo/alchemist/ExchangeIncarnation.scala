package it.unibo.alchemist

import javax.script.ScriptEngineManager

import com.github.benmanes.caffeine.cache.{ Caffeine, LoadingCache }
import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.model.{ Position as AlchemistPosition, * }
import it.unibo.field4s.alchemist.actions.RunScaFiProgram
import it.unibo.field4s.alchemist.device.ScaFiDevice
import org.apache.commons.math3.random.RandomGenerator

class ExchangeIncarnation[Position <: AlchemistPosition[Position]] extends Incarnation[Any, Position]:

  override def getProperty(node: Node[Any], molecule: Molecule, property: String): Double =
    val concentration = node.getConcentration(molecule)
    val result = property match
      case property if property.isEmpty || property.isBlank => concentration
      case property =>
        val f = ScalaScriptEngine.propertyCache.get(property).nn
        f(concentration)

    result match
      case double: Double => double
      case number: Number => number.doubleValue()
      case string: String => string.toDoubleOption.getOrElse(Double.NaN)
      case _ => Double.NaN

  override def createMolecule(s: String): Molecule = SimpleMolecule(s)

  override def createConcentration(s: String): Any =
    ScalaScriptEngine.concentrationCache.get(s)

  @SuppressWarnings(Array("DisableSyntax.null"))
  override def createConcentration(): Any = null

  override def createNode(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      parameter: String,
  ): Node[Any] =
    val node = GenericNode[Any](environment)
    node.addProperty(ScaFiDevice[Position, Any](node, environment, DoubleTime(1))) // TODO: take retention as parameter
    node

  override def createTimeDistribution(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      parameter: String,
  ): TimeDistribution[Any] =
    val frequency = parameter.toDoubleOption.getOrElse(1.0)
    val initialDelay = randomGenerator.nextDouble() / frequency
    DiracComb(DoubleTime(initialDelay), frequency)

  override def createReaction(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      timeDistribution: TimeDistribution[Any],
      parameter: String,
  ): Reaction[Any] =
    val event = Event[Any](node, timeDistribution)
    event.setActions(
      java.util.List.of(createAction(randomGenerator, environment, node, timeDistribution, event, parameter)),
    )
    event

  override def createCondition(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      time: TimeDistribution[Any],
      actionable: Actionable[Any],
      additionalParameters: String,
  ): Condition[Any] = new AbstractCondition[Any](node):
    override def getContext: Context = Context.LOCAL
    override def getPropensityContribution: Double = 1
    override def isValid: Boolean = true

  override def createAction(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      time: TimeDistribution[Any],
      actionable: Actionable[Any],
      additionalParameters: String,
  ): Action[Any] =
    RunScaFiProgram[Position](node, environment, time, additionalParameters)

  private object ScalaScriptEngine:
    private val engine = ScriptEngineManager().getEngineByName("scala").nn

    val concentrationCache: LoadingCache[String, Any] =
      Caffeine.newBuilder().nn.build[String, Any] { s => engine.eval(s) }.nn

    @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
    val propertyCache: LoadingCache[String, Any => Double] = Caffeine
      .newBuilder()
      .nn
      .build[String, Any => Double] { property =>
        engine.eval(property).asInstanceOf[Any => Double]
      }
      .nn
  end ScalaScriptEngine

end ExchangeIncarnation
