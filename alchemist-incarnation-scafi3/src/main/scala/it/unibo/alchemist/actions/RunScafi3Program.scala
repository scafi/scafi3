package it.unibo.alchemist.actions

import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.{Position as AlchemistPosition, *}
import it.unibo.alchemist.scafi.device.Scafi3Device
import it.unibo.scafi.alchemist.device.context.AlchemistExchangeContext
import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.runtime.ScafiEngine

/**
 * An Alchemist [[Action]] that runs a [[it.unibo.scafi.runtime.ScafiEngine]] program.
 *
 * @param node the node on which the action is executed.
 * @param programName the name of the program to run.
 * @tparam Position the position type of the node.
 */
class RunScafi3Program[Position <: AlchemistPosition[Position]](
    node: Node[Any],
    environment: Environment[Any, Position],
    val programName: String,
) extends AbstractAction[Any](node):
  private val programIdentifier = SimpleMolecule(programName)
  private val programPath: Array[String] = programName.split('.')
  private val classPath: String = programPath.take(programPath.length - 1).mkString("", ".", "$")
  private val clazz = Class.forName(classPath).nn
  private val module = clazz.getField("MODULE$").nn.get(clazz)
  private val method = clazz.getMethods.nn.toList.find(_.nn.getName.nn == programPath.last).get.nn

  val localDevice: Scafi3Device[Position] = node.asProperty(classOf[Scafi3Device[Position]])

  private val scafiProgram: ScafiEngine[Int, ? <: AggregateContext, Scafi3Device[Position], Any] = ScafiEngine(
    node.getId,
    localDevice,
    (_, net, state) => AlchemistExchangeContext[Position](node, environment, net.receive, state)
  )(runProgram)

  declareDependencyTo(programIdentifier)

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
  private def runProgram(using context: AggregateContext): Any =
    method.invoke(module, context)

  override def getContext: Context = Context.NEIGHBORHOOD

  override def cloneAction(node: Node[Any], reaction: Reaction[Any]): Action[Any] =
    RunScafi3Program[Position](node, environment, programName)

  override def execute(): Unit =
    val result = scafiProgram.cycle()
    node.setConcentration(programIdentifier, result)
end RunScafi3Program
