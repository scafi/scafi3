package it.unibo.scafi.test.environment

import scala.collection.mutable

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.runtime.network.NetworkManager

class Environment[R, Context <: AggregateContext { type DeviceId = Int }](
    val areConnected: (Environment[R, Context], Node[R, Context], Node[R, Context]) => Boolean,
    private val contextFactory: (Int, NetworkManager { type DeviceId = Int }) => Context,
    private val program: (Context, Environment[R, Context]) ?=> R,
    private val retainTime: Int = 1,
):
  private val positions = mutable.Map[Node[R, Context], Position]()
  private val random = scala.util.Random(0)
  private var nextNodeId = 0

  private def orderAndCycle(using ord: Ordering[Node[R, Context]]): Unit =
    val sortedNodes = positions.keys.toSeq.sorted(using ord)
    sortedNodes.foreach(_.cycle)

  /**
   * Retrieves the set of all nodes in the environment.
   *
   * @return
   *   A [[Set]] containing all the nodes present in the environment.
   */
  def nodes: Set[Node[R, Context]] = positions.keySet.toSet

  /**
   * Retrieves a map of all nodes and their corresponding positions in the environment.
   *
   * @return
   *   A [[Map]] where the keys are [[Node]] instances and the values are their respective [[Position]].
   */
  def nodesAndPositions: Map[Node[R, Context], Position] = positions.toMap

  /**
   * Adds a new node to the environment at the specified position.
   *
   * @param position
   *   The position where the new node will be placed.
   */
  def addNode(position: Position): Unit =
    val node = Node(this, nextNodeId, retainTime, contextFactory, program)
    positions(node) = position
    nextNodeId += 1

  /**
   * Retrieves a node by its unique identifier.
   *
   * @param id
   *   The unique identifier of the node.
   * @return
   *   An [[Option]] containing the node if found, or [[None]] if no node with the given ID exists.
   */
  def apply(id: Int): Option[Node[R, Context]] = positions.keys.find(_.id == id)

  /**
   * Retrieves the position of a node by its unique identifier.
   *
   * @param id
   *   The unique identifier of the node.
   * @return
   *   An [[Option]] containing the [[Position]] of the node if found, or [[None]] if no node with the given ID exists.
   */
  def positionOf(id: Int): Option[Position] =
    positions.keys.find(_.id == id).flatMap(positions.get)

  /**
   * Retrieves the position of a given node in the environment.
   *
   * @param node
   *   The node whose position is to be retrieved.
   * @return
   *   An [[Option]] containing the [[Position]] of the node if found, or [[None]] if the node does not exist.
   */
  def positionOf(node: Node[R, Context]): Option[Position] =
    positions.get(node)

  /**
   * Retrieves the neighbors of a given node in the environment.
   *
   * @param node
   *   The node for which neighbors are to be found.
   * @return
   *   A [[Set]] containing all nodes that are neighbors of the given node.
   */
  def neighborsOf(node: Node[R, Context]): Set[Node[R, Context]] =
    positions.keys.filter { other => other != node && areConnected(this, node, other) }.toSet

  /**
   * Removes a node from the environment by its unique identifier.
   *
   * @param id
   *   The unique identifier of the node to be removed.
   * @throws NoSuchElementException
   *   If no node with the given ID exists in the environment.
   */
  def removeNode(id: Int): Unit =
    positions.keys.find(_.id == id) match
      case Some(node) => val _ = positions.remove(node)
      case None => throw new NoSuchElementException(s"Node with ID $id not found in the environment.")

  /**
   * Executes a cycle for all nodes in the environment in ascending order of their IDs.
   */
  def cycleInOrder(): Unit = orderAndCycle(using Ordering.by[Node[R, Context], Int](_.id))

  /**
   * Executes a cycle for all nodes in the environment in descending order of their IDs.
   */
  def cycleInReverseOrder(): Unit = orderAndCycle(using Ordering.by[Node[R, Context], Int](_.id).reverse)

  /**
   * Executes a cycle for all nodes in the environment in a random order.
   */
  def cycleInRandomOrder(): Unit = orderAndCycle(using Ordering.by[Node[R, Context], Int](_ => random.nextInt()))

  /**
   * Retrieves the current status of all nodes in the environment.
   *
   * @return
   *   A [[Map]] where the keys are the unique identifiers of the nodes, and the values are the results of the last
   *   computation performed by each node.
   */
  def status: Map[Int, R] = positions.keys.map(node => node.id -> node.result).toMap
end Environment
