package it.unibo.alchemist

import javax.script.ScriptEngineManager
import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.model.{ Position as AlchemistPosition, * }
import com.github.benmanes.caffeine.cache.{ Caffeine, LoadingCache }
import it.unibo.alchemist.actions.RunScafi3Program
import it.unibo.alchemist.scafi.device.Scafi3Device
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet

class Scafi3Incarnation[Position <: AlchemistPosition[Position]] extends Incarnation[Any, Position]:

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
      case boolean: Boolean => if boolean then 1.0 else 0.0
      case _ => Double.NaN

  override def createMolecule(s: String): Molecule = SimpleMolecule(s)

  override def createConcentration(descriptor: Any): Any =
    ScalaScriptEngine.concentrationCache.get(descriptor.toString)

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
  override def createConcentration(): Any = null.asInstanceOf[Any]

  override def createAction(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      time: TimeDistribution[Any],
      actionable: Actionable[Any],
      additionalParameters: Any,
  ): Action[Any] =
    require(node != null, "Scafi3 requires a device and cannot execute in a Global Reaction")
    additionalParameters match
      case params: String => RunScafi3Program[Position](node, environment, params)
      case params =>
        throw IllegalArgumentException(
          s"Invalid parameters for Scafi3. `String` required, but ${params.getClass} has been provided: $params",
        )

  override def createCondition(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      time: TimeDistribution[Any],
      actionable: Actionable[Any],
      additionalParameters: Any,
  ): Condition[Any] =
    require(node != null, "Scafi3 requires a device to not be null")
    new AbstractCondition[Any](node):
      override def getContext: Context = Context.LOCAL
      override def getPropensityContribution: Double = 1.0
      override def isValid: Boolean = true

  override def createReaction(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      timeDistribution: TimeDistribution[Any],
      parameter: Any,
  ): Reaction[Any] =
    val event = Event[Any](node, timeDistribution)
    event.setActions(ListSet.of(createAction(randomGenerator, environment, node, timeDistribution, event, parameter)))
    event

  override def createTimeDistribution(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      node: Node[Any],
      parameter: Any | Null,
  ): TimeDistribution[Any] =
    val frequency = parameter match
      case param: Number => param.doubleValue()
      case param: String => param.toDoubleOption.getOrElse(1.0)
      case param =>
        throw new IllegalArgumentException(s"Invalid time distribution parameter of type ${param.getClass}: $param")
    val initialDelay = randomGenerator.nextDouble() / frequency
    DiracComb(DoubleTime(initialDelay), frequency)

  override def createNode(
      randomGenerator: RandomGenerator,
      environment: Environment[Any, Position],
      parameter: Any,
  ): Node[Any] =
    val node = GenericNode[Any](environment)
    val retention = parameter match
      case params: Number => DoubleTime(params.doubleValue())
      case params: String => DoubleTime(params.toDouble)
      case _ => null
    node.addProperty(Scafi3Device[Position](randomGenerator, environment, node, retention))
    node

  private object ScalaScriptEngine:
    private val engine = ScriptEngineManager().getEngineByName("scala").nn

    val concentrationCache: LoadingCache[String, Any] =
      Caffeine.newBuilder().nn.build[String, Any](engine.eval).nn

    @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
    val propertyCache: LoadingCache[String, Any => Double] = Caffeine
      .newBuilder()
      .nn
      .build[String, Any => Double] { property =>
        engine.eval(property).asInstanceOf[Any => Double]
      }
      .nn
  end ScalaScriptEngine
end Scafi3Incarnation
