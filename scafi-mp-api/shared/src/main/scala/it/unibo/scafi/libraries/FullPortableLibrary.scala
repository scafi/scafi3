package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax

/**
 * Aggregates all the portable libraries in a singe entry point trait.
 * @param lang
 *   the language instance providing the necessary syntaxes for all the libraries.
 */
trait FullPortableLibrary[L <: AggregateFoundation & BranchingSyntax & ExchangeSyntax](using lang: L)
    extends PortableCommonLibrary
    with PortableBranchingLibrary
    with PortableExchangeCalculusLibrary:
  ctx: PortableTypes =>

  override type Language = L

  override val language: Language = lang
