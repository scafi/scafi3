package it.unibo.scafi.test.environment

import scala.compiletime.uninitialized

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.{ Export, Import }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager

class Node[R, Context <: AggregateContext { type DeviceId = Int }](
    val environment: Environment[R, Context],
    val id: Int,
    val retain: Int,
    private val contextFactory: (Int, NetworkManager { type DeviceId = Int }) => Context,
    private val program: (Context, Environment[R, Context]) ?=> R,
):
  private val nodeNetworkManager = NodeNetworkManager()
  private var currentResult: R = uninitialized
  private val engine = ScafiEngine(id, nodeNetworkManager, contextFactory): ctx ?=>
    program(using ctx, environment)

  /**
   * Executes the round of the node on the given environment.
   * @return
   *   The result of the round.
   */
  def cycle: R =
    currentResult = engine.cycle()
    currentResult

  /**
   * Retrieves the last export result of the node.
   *
   * @return
   *   An [[Export]] containing the last export result of type [[Int]].
   */
  def lastExportResult: Export[Int] = engine.lastExportResult

  /**
   * Retrieves the current result of the node's computation.
   *
   * @return
   *   The result of type `R`.
   */
  def result: R = currentResult

  private class NodeNetworkManager extends NetworkManager:
    override type DeviceId = Int

    override def send(message: Export[Int]): Unit = () // In-memory message communication

    override def receive: Import[Int] =
      val neighborsValueTrees = environment
        .neighborsOf(Node.this)
        .map(neighbor => neighbor.id -> neighbor.lastExportResult(Node.this.id))
        .toMap
      Import(neighborsValueTrees)
end Node

object Node:
  given [R, Context <: AggregateContext { type DeviceId = Int }]: CanEqual[Node[R, Context], Node[R, Context]] =
    CanEqual.derived
