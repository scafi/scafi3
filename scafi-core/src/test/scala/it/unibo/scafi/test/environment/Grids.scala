package it.unibo.scafi.test.environment

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.message.ValueTree
import it.unibo.scafi.runtime.network.NetworkManager

type IntAggregateContext = AggregateContext { type DeviceId = Int }

type IntNetworkManager = NetworkManager { type DeviceId = Int }

object Grids:
  private val SAFE_MOORE_RADIUS = 1.5
  private val SAFE_VON_NEUMANN_RADIUS = 1.0

  extension [Result, Context <: IntAggregateContext, Network <: IntNetworkManager](
      environment: Environment[Result, Context, Network]
  )
    /**
     * Creates a grid of nodes in the environment.
     *
     * @param sizeX
     *   The number of nodes along the X-axis.
     * @param sizeY
     *   The number of nodes along the Y-axis.
     */
    def grid(sizeX: Int, sizeY: Int): Environment[Result, Context, Network] =
      for
        x <- 0 until sizeX
        y <- 0 until sizeY
      do environment.addNode(Position(x.toDouble, y.toDouble, 0.0))
      environment

  /**
   * Creates an environment with a Moore neighborhood grid of nodes.
   *
   * @param sizeX
   *   The number of nodes along the X-axis.
   * @param sizeY
   *   The number of nodes along the Y-axis.
   * @param contextFactory
   *   A function to create the context for each node, given its ID and a `NetworkManager`.
   * @param networkFactory
   *   A function to create the network manager for each node, given the node and the environment as context.
   * @param program
   *   The aggregate program to be executed by the nodes in the environment.
   * @tparam Result
   *   The type of the result produced by the aggregate program.
   * @tparam Context
   *   The type of the aggregate context, which must have `DeviceId` as `Int`.
   * @return
   *   An `Environment` containing the grid of nodes with Moore neighborhood connectivity.
   * @see
   *   [[Node.inMemoryNetwork]] for an in-memory network manager.
   */
  def mooreGrid[Result, Context <: IntAggregateContext, Network <: IntNetworkManager](
      sizeX: Int,
      sizeY: Int,
      contextFactory: (Int, Network, ValueTree) => Context,
      networkFactory: Environment[Result, Context, Network] ?=> Node[Result, Context, Network] => Network,
  )(
      program: (Context, Environment[Result, Context, Network]) ?=> Result,
  ): Environment[Result, Context, Network] = Environment[Result, Context, Network](
    areConnected = (env, n1, n2) =>
      (for n1Pos <- env.positionOf(n1); n2Pos <- env.positionOf(n2)
      yield n1Pos.distanceTo(n2Pos) <= SAFE_MOORE_RADIUS).getOrElse(false),
    contextFactory = contextFactory,
    program = program,
    networkFactory = networkFactory,
  ).grid(sizeX, sizeY)

  /**
   * Creates an environment with a Von Neumann neighborhood grid of nodes.
   *
   * @param sizeX
   *   The number of nodes along the X-axis.
   * @param sizeY
   *   The number of nodes along the Y-axis.
   * @param contextFactory
   *   A function to create the context for each node, given its ID and a `NetworkManager`.
   * @param networkFactory
   *   A function to create the network manager for each node, given the node and the environment as context.
   * @param program
   *   The aggregate program to be executed by the nodes in the environment.
   * @tparam Result
   *   The type of the result produced by the aggregate program.
   * @tparam Context
   *   The type of the aggregate context, which must have `DeviceId` as `Int`.
   * @return
   *   An `Environment` containing the grid of nodes with Von Neumann neighborhood connectivity.
   * @see
   *   [[Node.inMemoryNetwork]] for an in-memory network manager.
   */
  def vonNeumannGrid[Result, Context <: IntAggregateContext, Network <: IntNetworkManager](
      sizeX: Int,
      sizeY: Int,
      contextFactory: (Int, Network, ValueTree) => Context,
      networkFactory: Environment[Result, Context, Network] ?=> Node[Result, Context, Network] => Network,
  )(
      program: (Context, Environment[Result, Context, Network]) ?=> Result,
  ): Environment[Result, Context, Network] = Environment[Result, Context, Network](
    areConnected = (env, n1, n2) =>
      (for n1Pos <- env.positionOf(n1); n2Pos <- env.positionOf(n2)
      yield n1Pos.distanceTo(n2Pos) <= SAFE_VON_NEUMANN_RADIUS).getOrElse(false),
    contextFactory = contextFactory,
    program = program,
    networkFactory = networkFactory,
  ).grid(sizeX, sizeY)

  /**
   * Creates a Von Neumann grid. Each device is identified by an integer id from `0` to `rows * cols - 1`, assigned in
   * row-major order (i.e., left to right, top to bottom).
   * @param rows
   *   the number of rows of the grid
   * @param cols
   *   the number of columns of the grid
   * @param f
   *   a function that takes a device id and its neighbors' ids and returns a result
   * @return
   *   a sequence of results, one for each device in the grid
   */
  def vonNeumannGrid[Result](rows: Int, cols: Int)(f: (Int, Set[Int]) => Result): Seq[Result] =
    val areConnected = (a: Int, b: Int) =>
      val (ax, ay) = (a / cols, a % cols)
      val (bx, by) = (b / cols, b % cols)
      Position(ax, ay, 0.0).distanceTo(Position(bx, by, 0.0)) <= SAFE_VON_NEUMANN_RADIUS
    for
      i <- 0 until rows * cols
      neighbors = (0 until rows * cols).filter(areConnected(i, _)).toSet
    yield f(i, neighbors)
end Grids
