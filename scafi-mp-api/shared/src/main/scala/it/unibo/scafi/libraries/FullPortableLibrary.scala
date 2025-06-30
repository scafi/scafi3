package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax

/**
 * Aggregates all the portable libraries in a singe entry point trait.
 * @param lang
 *   the language instance providing the necessary syntaxes for all the libraries.
 */
trait FullPortableLibrary[Lang <: AggregateFoundation & BranchingSyntax & ExchangeSyntax](using lang: Lang)
    extends PortableCommonLibrary
    with PortableBranchingLibrary
    with PortableExchangeCalculusLibrary:
  ctx: PortableTypes =>

  override type Language = Lang

  override val language: Language = lang
