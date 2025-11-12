package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.types.PortableTypes

/**
 * Aggregates all the portable libraries in a singe entry point trait.
 * @param lang
 *   the language instance providing the necessary syntaxes for all the libraries.
 */
trait FullPortableLibrary[Lang <: AggregateFoundation & BranchingSyntax & FieldCalculusSyntax](using
    lang: Lang,
) extends PortableCommonLibrary
    with PortableBranchingLibrary
    with PortableFieldCalculusLibrary:
  self: PortableTypes =>

  override type Language = Lang

  override val language: Language = lang

  inline override def branch[Value](condition: Boolean)(trueBranch: Function0[Value])(
      falseBranch: Function0[Value],
  ): Value =
    language.branch(condition)(trueBranch())(falseBranch())

  inline override def evolve[Value](initial: Value)(evolution: Function1[Value, Value]): Value =
    language.evolve(initial)(evolution)

  inline override def neighborValues[Value](expr: Value): SharedData[Value] =
    language.neighborValues(expr)(using valueCodable(expr))

  inline override def share[Value](initial: Value)(shareAndReturning: Function1[SharedData[Value], Value]): Value =
    language.share(initial)(shareAndReturning(_))(using valueCodable(initial))
end FullPortableLibrary
