package it.unibo.scafi.test.environment

import scala.annotation.unused
import scala.compiletime.uninitialized

import it.unibo.scafi.message.{ Export, Import, ValueTree }
import it.unibo.scafi.runtime.ScafiEngine
import it.unibo.scafi.runtime.network.NetworkManager

class Node[Result, Context <: IntAggregateContext, Network <: IntNetworkManager](
    val environment: Environment[Result, Context, Network],
    val id: Int,
    val retain: Int,
    private val contextFactory: (Network, ValueTree) => Context,
    private val program: (Context, Environment[Result, Context, Network]) ?=> Result,
    private val networkManagerFactory: Node[Result, Context, Network] => Network,
):
  lazy val networkManager: Network = networkManagerFactory(this)
  private var currentResult: Result = uninitialized
  private lazy val engine = ScafiEngine(networkManager, contextFactory): ctx ?=>
    program(using ctx, environment)

  /**
   * Executes the round of the node on the given environment.
   * @return
   *   The result of the round.
   */
  def cycle: Result =
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
   *   The result of type `Result`.
   */
  def result: Result = currentResult
end Node

object Node:
  given [Result, Context <: IntAggregateContext, Network <: IntNetworkManager]
      : CanEqual[Node[Result, Context, Network], Node[Result, Context, Network]] = CanEqual.derived

  def inMemoryNetwork[Result, Context <: IntAggregateContext, Network <: IntNetworkManager](using
      @unused("This simple in-memory network does not use Environment directly, but other networks may")
      env: Environment[Result, Context, Network],
  )(node: Node[Result, Context, Network]): IntNetworkManager = InMemoryNetwork(node)

  private class InMemoryNetwork[Result, Context <: IntAggregateContext, Network <: IntNetworkManager](
      node: Node[Result, Context, Network],
  ) extends NetworkManager:
    override type DeviceId = Int

    override val localId: Int = node.id

    override def send(message: Export[Int]): Unit = () // In-memory message communication

    override def receive: Import[Int] =
      val neighborsValueTrees = node.environment
        .neighborsOf(node)
        .map(neighbor => neighbor.id -> neighbor.lastExportResult(node.id))
        .toMap
      Import(neighborsValueTrees)

    override def deliverableReceived(from: Int, message: ValueTree): Unit = ()
end Node
