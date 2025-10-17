package it.unibo.alchemist

import javax.script.ScriptEngineManager
import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.model.{Position as AlchemistPosition, *}
import com.github.benmanes.caffeine.cache.{Caffeine, LoadingCache}
import dotty.tools.dotc.Main
import it.unibo.alchemist.Scafi3Incarnation.CACHE_SIZE
import it.unibo.alchemist.actions.RunScafi3Program
import it.unibo.alchemist.scafi.device.Scafi3Device
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet
import org.slf4j.LoggerFactory

import java.net.URLClassLoader
import java.nio.file.Files
import java.io.File

class Scafi3Incarnation[T, Position <: AlchemistPosition[Position]] extends Incarnation[T, Position]:
  private val logger = LoggerFactory.getLogger(getClass)
  private val classLoaders: LoadingCache[String, URLClassLoader] =
    Caffeine.newBuilder().maximumSize(CACHE_SIZE).build: name =>
      val outputFolder = Files.createTempDirectory(name).toFile
      URLClassLoader(Array(outputFolder.toURI.toURL), Thread.currentThread().getContextClassLoader)

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
      case params: java.util.Map[String, String] @unchecked =>
        val code = params.get("code")
        val entrypoint = params.get("entrypoint")
        val (hasErrors, errors) = compileScafiProgram(code, classLoaders.get(entrypoint))
        if hasErrors then
          throw IllegalArgumentException(
            s"Could not compile Scafi3 program:\n${errors.mkString("\n")}",
          )
        RunScafi3Program[T, Position](node, environment, entrypoint, Some(classLoaders.get(entrypoint)))
      case params =>
        throw IllegalArgumentException(
          s"Invalid parameters for Scafi3. `String` required, but ${params.getClass} has been provided: $params",
        )

  private def compileScafiProgram(code: String, classLoader: URLClassLoader): (Boolean, List[String]) =
    logger.info("Compiling Scafi3 program into folder {}", classLoader.getURLs.head.getPath)
    val outputFolder = File(classLoader.getURLs.head.getFile).toPath
    require(outputFolder.toFile.exists() && outputFolder.toFile.isDirectory, s"Output folder $outputFolder does not exist or is not a directory")
    val sourceFilePath = Files.writeString(outputFolder.resolve("Program.scala"), code)
    // Compile file
    compileWithOptions(sourceFilePath.toAbsolutePath.toString, outputFolder.toAbsolutePath.toString)

  private def compileWithOptions(sourceFile: String, outputDir: String): (Boolean, List[String]) =
    val cp = Thread.currentThread().getContextClassLoader match
      case urlClassLoader: URLClassLoader => urlClassLoader.getURLs.map(_.getPath).mkString(File.pathSeparator)
      case _                             => System.getProperty("java.class.path")
    val args = Array(
      "-d", outputDir,       // Destination folder
      "-classpath", cp,      // Classpath from current thread
      "-deprecation",        // Show deprecation warnings
      "-explain",            // Explain errors in detail
      "-experimental",
      sourceFile
    )
    val reporter = Main.process(args)
    (reporter.hasErrors, List(reporter.summary))

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

object Scafi3Incarnation:
  private val CACHE_SIZE = 1000