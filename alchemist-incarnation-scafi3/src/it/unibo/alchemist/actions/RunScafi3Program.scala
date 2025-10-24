package it.unibo.alchemist.actions

import java.net.URLClassLoader

import it.unibo.alchemist.model.{ Position as AlchemistPosition, * }
import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.scafi.device.Scafi3Device
import it.unibo.scafi.alchemist.device.context.AlchemistExchangeContext
import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.runtime.ScafiEngine

/**
 * An Alchemist [[Action]] that runs a [[it.unibo.scafi.runtime.ScafiEngine]] program.
 *
 * @param node
 *   the node on which the action is executed.
 * @param programName
 *   the name of the program to run.
 * @tparam Position
 *   the position type of the node.
 */
class RunScafi3Program[T, Position <: AlchemistPosition[Position]](
    node: Node[T],
    environment: Environment[T, Position],
    val programName: String,
    val classLoader: Option[URLClassLoader] = None,
) extends AbstractAction[T](node):
  private val programIdentifier = SimpleMolecule(programName)
  private val programPath: Array[String] = programName.split('.')
  private val classPath: String = programPath.take(programPath.length - 1).mkString("", ".", "$")
  private val clazz = classLoader.map(_.loadClass(classPath)).getOrElse(Class.forName(classPath))
  private val module = clazz.getField("MODULE$").nn.get(clazz)
  private val methods = clazz.getMethods.nn
  private val method = methods.toList.find(_.nn.getName.nn == programPath.last).get.nn

  val localDevice: Scafi3Device[T, Position] = node.asProperty(classOf[Scafi3Device[T, Position]])

  private val scafiProgram: ScafiEngine[Int, ? <: AggregateContext, Scafi3Device[T, Position], T] = ScafiEngine(
    node.getId,
    localDevice,
    (_, net, state) => AlchemistExchangeContext[T, Position](node, environment, net.receive, state),
  )(runProgram)

  declareDependencyTo(programIdentifier)

  override def getContext: Context = Context.NEIGHBORHOOD

  override def cloneAction(node: Node[T], reaction: Reaction[T]): Action[T] =
    RunScafi3Program[T, Position](node, environment, programName)

  override def execute(): Unit =
    val result = scafiProgram.cycle()
    node.setConcentration(programIdentifier, result)

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
  private def runProgram(using context: AggregateContext): T =
    method.invoke(module, context).asInstanceOf[T]

end RunScafi3Program
