package it.unibo.alchemist.actions

import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.{Position as AlchemistPosition, *}
import it.unibo.scafi.alchemist.AlchemistContext
import it.unibo.scafi.alchemist.device.ScaFiDevice
import it.unibo.scafi.runtime.ScafiEngine

/**
 * An Alchemist [[Action]] that runs a [[it.unibo.scafi.runtime.ScafiEngine]] program.
 * 
 * @param node the node on which the action is executed.
 * @param programName the name of the program to run.
 * @tparam T the result type of the program.
 * @tparam Position the position type of the node.
 */
class RunScafi3Program[T, Position <: AlchemistPosition[Position]](
    node: Node[T],
    val programName: String,
) extends AbstractAction[T](node):
  private val programIdentifier = SimpleMolecule(programName)
  
  val localDevice: ScaFiDevice[T, Position] = node.asProperty(classOf[ScaFiDevice[T, Position]])
  
  lazy val scafiProgram: ScafiEngine[Int, AlchemistContext[T, Position], ScaFiDevice[T, Position], T] = ScafiEngine(
    node.getId,
    localDevice,
    (id, net, state) => AlchemistContext(id, net.receive, state)
  )(???)
  
  declareDependencyTo(programIdentifier)
  
//  private val programPath: Array[String] = program.split('.')
//  private val classPath: String = programPath.take(programPath.length - 1).mkString("", ".", "$")
//  private val clazz = Class.forName(classPath).nn
//  private val module = clazz.getField("MODULE$").nn.get(clazz)
//  private val method = clazz.getMethods.nn.toList.find(_.nn.getName.nn == programPath.last).get.nn

//  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
//  private def runProgram(using context: AlchemistContext[T, Position]): Any =
//    method.invoke(module, context).nn.asInstanceOf[Any]

//  private object Factory
//      extends ContextFactory[ScaFiDevice[T, Position, AlchemistContext.ExportValue], AlchemistContext[T, Position]]:

//    override def create(
//        network: ScaFiDevice[T, Position, AlchemistContext.ExportValue],
//    ): AlchemistContext[T, Position] =
//      scafi.alchemist.AlchemistContext(
//        environment,
//        network.localId,
//        network.receive(),
//      )
//
//  private val engine = Engine(
//    network = node.asProperty(classOf[ScaFiDevice[T, Position, AlchemistContext.ExportValue]]),
//    factory = Factory,
//    program = runProgram,
//  )
//
//  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
//  override def execute(): Unit =
//    val result = engine.cycle()
//    node.setConcentration(SimpleMolecule(programPath.last), result.asInstanceOf[T])
//
//  override def cloneAction(node: Node[T], reaction: Reaction[T]): Action[T] =
//    RunScaFiProgram[T, Position](node, environment, time, program)

  override def getContext: Context = Context.NEIGHBORHOOD

  override def cloneAction(node: Node[T], reaction: Reaction[T]): Action[T] =
    RunScafi3Program[T, Position](node, programName)

  override def execute(): Unit =
    val result = scafiProgram.cycle()
    node.setConcentration(programIdentifier, result)
end RunScafi3Program
