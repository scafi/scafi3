package it.unibo.scafi.libraries

import it.unibo.scafi.utils.boundaries.CommonBoundaries

/**
 * This object is a container for all the standard libraries available. It is useful to import all the libraries with a
 * single import.
 */
object All:
  export BranchingLibrary.{ *, given }
  export CommonLibrary.{ *, given }
  export ExchangeCalculusLibrary.{ *, given }
  export FieldCalculusLibrary.{ *, given }
  export FoldingLibrary.{ *, given }
  export GradientLibrary.{ *, given }
  export MathLibrary.{ *, given }
  export CommonBoundaries.{ *, given }
  export cats.syntax.all.*
