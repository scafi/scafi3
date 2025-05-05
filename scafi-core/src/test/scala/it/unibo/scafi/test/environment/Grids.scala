package it.unibo.scafi.test.environment

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.runtime.network.NetworkManager

object Grids:
  private val SAFE_MOORE_RADIUS = 1.5
  private val SAFE_VON_NEUMANN_RADIUS = 1.0

  type IntAggregateContext = AggregateContext { type DeviceId = Int }

  extension [R, Context <: IntAggregateContext](environment: Environment[R, Context])
    /**
     * Creates a grid of nodes in the environment.
     *
     * @param sizeX
     *   The number of nodes along the X-axis.
     * @param sizeY
     *   The number of nodes along the Y-axis.
     */
    def grid(sizeX: Int, sizeY: Int): Environment[R, Context] =
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
   * @param factory
   *   A function to create the context for each node, given its ID and a `NetworkManager`.
   * @param program
   *   The aggregate program to be executed by the nodes in the environment.
   * @tparam R
   *   The type of the result produced by the aggregate program.
   * @tparam Context
   *   The type of the aggregate context, which must have `DeviceId` as `Int`.
   * @return
   *   An `Environment` containing the grid of nodes with Moore neighborhood connectivity.
   */
  def mooreGrid[R, Context <: IntAggregateContext](
      sizeX: Int,
      sizeY: Int,
      factory: (Int, NetworkManager { type DeviceId = Int }) => Context,
  )(
      program: (Context, Environment[R, Context]) ?=> R,
  ): Environment[R, Context] = Environment[R, Context](
    areConnected = (env, n1, n2) =>
      (for n1Pos <- env.positionOf(n1); n2Pos <- env.positionOf(n2)
      yield n1Pos.distanceTo(n2Pos) <= SAFE_MOORE_RADIUS).getOrElse(false),
    contextFactory = factory,
    program = program,
  ).grid(sizeX, sizeY)

  /**
   * Creates an environment with a Von Neumann neighborhood grid of nodes.
   *
   * @param sizeX
   *   The number of nodes along the X-axis.
   * @param sizeY
   *   The number of nodes along the Y-axis.
   * @param factory
   *   A function to create the context for each node, given its ID and a `NetworkManager`.
   * @param program
   *   The aggregate program to be executed by the nodes in the environment.
   * @tparam R
   *   The type of the result produced by the aggregate program.
   * @tparam Context
   *   The type of the aggregate context, which must have `DeviceId` as `Int`.
   * @return
   *   An `Environment` containing the grid of nodes with Von Neumann neighborhood connectivity.
   */
  def vonNeumannGrid[R, Context <: IntAggregateContext](
      sizeX: Int,
      sizeY: Int,
      factory: (Int, NetworkManager { type DeviceId = Int }) => Context,
  )(
      program: (Context, Environment[R, Context]) ?=> R,
  ): Environment[R, Context] = Environment[R, Context](
    areConnected = (env, n1, n2) =>
      (for n1Pos <- env.positionOf(n1); n2Pos <- env.positionOf(n2)
      yield n1Pos.distanceTo(n2Pos) <= SAFE_VON_NEUMANN_RADIUS).getOrElse(false),
    contextFactory = factory,
    program = program,
  ).grid(sizeX, sizeY)
end Grids
