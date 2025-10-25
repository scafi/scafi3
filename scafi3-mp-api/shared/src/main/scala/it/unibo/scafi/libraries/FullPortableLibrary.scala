package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.types.PortableTypes

/**
 * Aggregates all the portable libraries in a singe entry point trait.
 * @param lang
 *   the language instance providing the necessary syntaxes for all the libraries.
 */
trait FullPortableLibrary[Lang <: AggregateFoundation & BranchingSyntax & ExchangeSyntax & FieldCalculusSyntax](using
    lang: Lang,
) extends PortableCommonLibrary
    with PortableBranchingLibrary
    with PortableExchangeCalculusLibrary
    with PortableFieldCalculusLibrary:
  self: PortableTypes =>

  override type Language = Lang

  override val language: Language = lang
