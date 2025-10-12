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

class Scafi3Incarnation[T, Position <: AlchemistPosition[Position]] extends Incarnation[T, Position]:
  override def createMolecule(s: String): Molecule = SimpleMolecule(s)

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.null"))
  override def createConcentration(): T = null.asInstanceOf[T]

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
      case boolean: Boolean => if boolean then 1.0 else 0.0
      case _ => Double.NaN

  override def createConcentration(descriptor: Any): T =
    ScalaScriptEngine.concentrationCache.get(descriptor.toString).asInstanceOf[T]

  override def createNode(randomGenerator: RandomGenerator, environment: Environment[T, Position], parameter: Any): Node[T] =
    val node = GenericNode(environment)
    val retention = parameter match
      case params: Number => DoubleTime(params.doubleValue())
      case params: String => DoubleTime(params.toDouble)
      case _ => null
    node.addProperty(Scafi3Device[T, Position](randomGenerator, environment, node, retention))
    node

  override def createTimeDistribution(randomGenerator: RandomGenerator, environment: Environment[T, Position], node: Node[T], parameter: Any): TimeDistribution[T] =
    val frequency = parameter match
      case param: Number => param.doubleValue()
      case param: String => param.toDoubleOption.getOrElse(1.0)
      case param =>
        throw new IllegalArgumentException(s"Invalid time distribution parameter of type ${param.getClass}: $param")
    val initialDelay = randomGenerator.nextDouble() / frequency
    DiracComb(DoubleTime(initialDelay), frequency)

  override def createReaction(randomGenerator: RandomGenerator, environment: Environment[T, Position], node: Node[T], timeDistribution: TimeDistribution[T], parameter: Any): Reaction[T] =
    val event = Event(node, timeDistribution)
    event.setActions(ListSet.of(createAction(randomGenerator, environment, node, timeDistribution, event, parameter)))
    event

  override def createCondition(randomGenerator: RandomGenerator, environment: Environment[T, Position], node: Node[T], time: TimeDistribution[T], actionable: Actionable[T], additionalParameters: Any): Condition[T] =
    require(node != null, "Scafi3 requires a device to not be null")
    new AbstractCondition(node):
      override def getContext: Context = Context.LOCAL
      override def getPropensityContribution: Double = 1.0
      override def isValid: Boolean = true

  override def createAction(randomGenerator: RandomGenerator, environment: Environment[T, Position], node: Node[T], time: TimeDistribution[T], actionable: Actionable[T], additionalParameters: Any): Action[T] =
    require(node != null, "Scafi3 requires a device and cannot execute in a Global Reaction")
    additionalParameters match
      case params: String => RunScafi3Program[T, Position](node, environment, params)
      case params =>
        throw IllegalArgumentException(
          s"Invalid parameters for Scafi3. `String` required, but ${params.getClass} has been provided: $params",
        )

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
