package it.unibo.scafi.language.common

/**
 * Language capability for conditionally including a sub-computation in the outbound message. When `keep` returns
 * `false` the exchange state written by `body` is rolled back so that aligned neighbours do not see this device's state
 * for the discarded scope.
 *
 * This is the scafi3 equivalent of the legacy `exportConditionally` / `vm.newExportStack` + `mergeExport` /
 * `discardExport` mechanism, and is the primitive required for correct `sspawn` semantics (devices outside a process
 * bubble must not pollute alignment inside the bubble).
 */
trait ConditionalExportLanguage:

  /**
   * Runs `body` and, if `keep(result)` is `false`, rolls back every exchange write produced by `body` so that
   * neighbours do not see this device's state for the body's alignment scope. The result is always returned
   * regardless.
   *
   * @param keep
   *   predicate on the body's result; `false` ⇒ discard this round's export
   * @param body
   *   the computation to run (and conditionally export)
   * @tparam T
   *   result type
   * @return
   *   the result of `body` whether kept or discarded
   */
  def conditionallyExport[T](keep: T => Boolean)(body: () => T): T
end ConditionalExportLanguage
