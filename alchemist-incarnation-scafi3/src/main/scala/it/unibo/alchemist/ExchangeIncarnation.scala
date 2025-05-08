package it.unibo.alchemist

import javax.script.ScriptEngineManager

import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.model.{ Position as AlchemistPosition, * }
import it.unibo.scafi.alchemist.actions.RunScaFiProgram
import it.unibo.scafi.alchemist.device.ScaFiDevice

import com.github.benmanes.caffeine.cache.{ Caffeine, LoadingCache }
import org.apache.commons.math3.random.RandomGenerator

class ExchangeIncarnation[T, Position <: AlchemistPosition[Position]] extends Incarnation[T, Position]:

  override def getProperty(node: Node[T], molecule: Molecule, property: String): Double =
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  override def createConcentration(descriptor: Any): T =
    ScalaScriptEngine.concentrationCache.get(descriptor.toString).asInstanceOf[T]

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
  override def createConcentration(): T = null.asInstanceOf[T]

  override def createNode(
      randomGenerator: RandomGenerator,
      environment: Environment[T, Position],
      parameter: Any,
  ): Node[T] =
    val node = GenericNode[T](environment)
    node.addProperty(
      ScaFiDevice[T, Position, Any](node, environment, DoubleTime(1)),
    ) // TODO: take retention as parameter
    node

  override def createTimeDistribution(
      randomGenerator: RandomGenerator,
      environment: Environment[T, Position],
      node: Node[T],
      parameter: Any,
  ): TimeDistribution[T] =
    val frequency = parameter match
      case param: String => param.toDoubleOption.getOrElse(1.0)
      case param: Number => param.doubleValue()
      case _ => throw new IllegalArgumentException(s"Type ${parameter.getClass.getSimpleName} not supported")
    val initialDelay = randomGenerator.nextDouble() / frequency
    DiracComb(DoubleTime(initialDelay), frequency)

  override def createReaction(
      randomGenerator: RandomGenerator,
      environment: Environment[T, Position],
      node: Node[T],
      timeDistribution: TimeDistribution[T],
      parameter: Any,
  ): Reaction[T] =
    val event = Event[T](node, timeDistribution)
    event.setActions(
      java.util.List.of(createAction(randomGenerator, environment, node, timeDistribution, event, parameter)),
    )
    event

  override def createCondition(
      randomGenerator: RandomGenerator,
      environment: Environment[T, Position],
      node: Node[T],
      time: TimeDistribution[T],
      actionable: Actionable[T],
      additionalParameters: Any,
  ): Condition[T] = new AbstractCondition[T](node):
    override def getContext: Context = Context.LOCAL
    override def getPropensityContribution: Double = 1
    override def isValid: Boolean = true

  @SuppressWarnings(Array("scalafix:DisableSyntax.isInstanceOf"))
  override def createAction(
      randomGenerator: RandomGenerator,
      environment: Environment[T, Position],
      node: Node[T],
      time: TimeDistribution[T],
      actionable: Actionable[T],
      additionalParameters: Any,
  ): Action[T] =
    require(additionalParameters.isInstanceOf[String], "Additional parameters must be a string for the program to run")
    RunScaFiProgram[T, Position](node, environment, time, additionalParameters.toString)

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
