package it.unibo.scafi.libraries
import it.unibo.scafi.types.PortableTypes

/**
 * The root base trait for all portable libraries.
 */
trait PortableLibrary:
  self: PortableTypes =>
  export it.unibo.scafi.language.AggregateFoundation
  export it.unibo.scafi.message.UniversalCodable

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
  given [Value]: Iso[SharedData[Value], language.SharedData[Value]] = compiletime.deferred

  given deviceIdConv[ID]: Conversion[language.DeviceId, ID] = compiletime.deferred

  /**
   * The universal codable instance used for encoding and decoding values to be sent over the network.
   */
  given valueCodable[Value, Format]: UniversalCodable[Value, Format] = compiletime.deferred
end PortableLibrary
