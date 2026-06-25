package it.unibo.scafi.context.common

import it.unibo.scafi.language.common.ConditionalExportLanguage
import it.unibo.scafi.message.OutboundMessage

/**
 * Concrete implementation of [[ConditionalExportLanguage]] that delegates to [[OutboundMessage.withRollbackUnless]].
 */
trait ConditionalExportContext extends ConditionalExportLanguage:
  self: OutboundMessage =>

  override def conditionallyExport[T](keep: T => Boolean)(body: () => T): T =
    withRollbackUnless(keep)(body())
