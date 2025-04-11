package it.unibo.scafi.alchemist.device

import it.unibo.alchemist.model.Time

case class TimedMessage[Value](time: Time, message: Value)
