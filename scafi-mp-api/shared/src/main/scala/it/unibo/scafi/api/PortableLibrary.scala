package it.unibo.scafi.api

/**
 * The root base trait for all portable libraries.
 */
trait PortableLibrary:
  ctx: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation

  /**
   * The language type comprising all the needed syntaxes needed to implement the library logic.
   */
  type Language <: AggregateFoundation

  /**
   * The [[Language]] instance used by the library to which delegate the syntax operations.
   */
  val language: Language

  /**
   * An equivalent definition of the [[language.SharedData]] data structure portable across platforms.
   */
  type PortableSharedData[Value]

  /**
   * [[PortableSharedData]] is isomorphic to [[language.SharedData]].
   */
  given [T]: Iso[PortableSharedData[T], language.SharedData[T]] = compiletime.deferred
end PortableLibrary
