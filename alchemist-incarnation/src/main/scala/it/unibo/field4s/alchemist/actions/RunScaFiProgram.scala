package it.unibo.field4s.alchemist.actions

import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.{Position as AlchemistPosition, *}
import it.unibo.field4s
import it.unibo.field4s.alchemist.AlchemistContext
import it.unibo.field4s.alchemist.device.ScaFiDevice
import it.unibo.field4s.alchemist
import it.unibo.field4s.engine.Engine
import it.unibo.field4s.engine.context.ContextFactory

class RunScaFiProgram[Position <: AlchemistPosition[Position]](
    val node: Node[Any],
    val environment: Environment[Any, Position],
    val time: TimeDistribution[Any],
    val program: String,
) extends AbstractAction[Any](node):
  private val programPath: Array[String] = program.split('.')
  private val classPath: String = programPath.take(programPath.length - 1).mkString("", ".", "$")
  private val clazz = Class.forName(classPath).nn
  private val module = clazz.getField("MODULE$").nn.get(clazz)
  private val method = clazz.getMethods.nn.toList.find(_.nn.getName.nn == programPath.last).get.nn

  @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
  private def runProgram(using context: AlchemistContext[Position]): Any =
    method.invoke(module, context).nn.asInstanceOf[Any]

  private object Factory
      extends ContextFactory[ScaFiDevice[Position, AlchemistContext.ExportValue], AlchemistContext[Position]]:

    override def create(network: ScaFiDevice[Position, AlchemistContext.ExportValue]): AlchemistContext[Position] =
      field4s.alchemist.AlchemistContext(
        environment,
        network.localId,
        network.receive(),
      )

  private val engine = Engine(
    network = node.asProperty(classOf[ScaFiDevice[Position, AlchemistContext.ExportValue]]),
    factory = Factory,
    program = runProgram,
  )

  override def execute(): Unit =
    val result = engine.cycle()
    node.setConcentration(SimpleMolecule(programPath.last), result)

  override def cloneAction(node: Node[Any], reaction: Reaction[Any]): Action[Any] =
    RunScaFiProgram[Position](node, environment, time, program)

  override def getContext: Context = Context.NEIGHBORHOOD
end RunScaFiProgram
