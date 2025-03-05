package it.unibo.field4s.alchemist

import it.unibo.alchemist.model.Time

object TimeUtils:
  given Ordering[Time] = _.compareTo(_)
