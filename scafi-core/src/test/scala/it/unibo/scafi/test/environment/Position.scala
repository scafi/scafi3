package it.unibo.scafi.test.environment

final case class Position(x: Double, y: Double, z: Double):
  /**
   * Computes the distance between two positions.
   * @param other
   *   the other position.
   * @return
   *   the distance between the two positions.
   */
  def distanceTo(other: Position): Double =
    math.sqrt(math.pow(x - other.x, 2) + math.pow(y - other.y, 2) + math.pow(z - other.z, 2))

  def toTuple: (Double, Double, Double) = (x, y, z)

object Position:
  /**
   * Creates a position from a tuple of coordinates.
   */
  extension (tuple: (Double, Double, Double)) def toPosition: Position = Position(tuple._1, tuple._2, tuple._3)

  /**
   * Creates a position from a tuple of coordinates (2D).
   */
  extension (tuple: (Double, Double)) def toPosition: Position = Position(tuple._1, tuple._2, 0.0)
