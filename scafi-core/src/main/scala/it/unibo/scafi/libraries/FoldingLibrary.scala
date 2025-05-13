package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation

object FoldingLibrary:

  extension [T](using language: AggregateFoundation)(av: language.SharedData[T])

    /**
     * Fold (accumulate) the aggregate values using the given function, including the value for self (if present).
     * @param base
     *   the base value
     * @param f
     *   the function that accumulates the values
     * @tparam A
     *   the type of the base value
     * @return
     *   the result of the accumulation
     */
    def fold[A](base: A)(f: (A, T) => A): A = av.foldLeft(base)(f)

    /**
     * Fold (accumulate) the aggregate values using the given function, excluding the value for self.
     * @param base
     *   the base value
     * @param f
     *   the function that accumulates the values
     * @tparam A
     *   the type of the base value
     * @return
     *   the result of the accumulation
     */
    def foldWithoutSelf[A](base: A)(f: (A, T) => A): A = av.withoutSelf.foldLeft(base)(f)
  end extension
end FoldingLibrary
