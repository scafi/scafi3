package it.unibo.scafi.libraries

/**
 * The root base trait for all portable libraries.
 */
trait PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation

  /**
   * The language type comprising all the needed syntaxes needed to implement the library functionalities.
   */
  type Language <: AggregateFoundation

  /**
   * The [[Language]] instance used by the library to which delegate the syntax operations.
   */
  val language: Language

  /**
   * A portable, semantically equivalent definition of the [[language.SharedData]] data structure.
   */
  type SharedData[Value]

  /**
   * [[SharedData]] is isomorphic to [[language.SharedData]].
   */
  given [T]: Iso[SharedData[T], language.SharedData[T]] = compiletime.deferred
end PortableLibrary
