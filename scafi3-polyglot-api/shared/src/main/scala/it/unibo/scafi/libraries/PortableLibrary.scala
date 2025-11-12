package it.unibo.scafi.libraries
import it.unibo.scafi.message.Codable
import it.unibo.scafi.types.PortableTypes

/**
 * The root base trait for all portable libraries.
 */
trait PortableLibrary:
  self: PortableTypes =>
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
  given [Value]: Iso[SharedData[Value], language.SharedData[Value]] = compiletime.deferred

  /**
   * A conversion from the [[language.DeviceId]] type to a generic type `ID`, to be specialized by concrete
   * implementations.
   */
  given deviceIdConversion[ID]: Conversion[language.DeviceId, ID] = compiletime.deferred

  /**
   * The conversion from a value of type `Value` to a codable representation of it in format `Format`.
   */
  given valueCodable[Value, Format]: Conversion[Value, Codable[Value, Format]] = compiletime.deferred
end PortableLibrary
