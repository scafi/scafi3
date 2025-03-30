package it.unibo.scafi.engine.context

import it.unibo.scafi.collections.{ MapWithDefault, ValueTree }
import it.unibo.scafi.engine.network.{ Export, Import, Network }

class ValueTreeTestingNetwork[Id, Token, Value](
    var localId: Id,
    var received: Import[Id, ValueTree[Token, Value]] = Map.empty,
) extends Network[Id, ValueTree[Token, Value]]:
  var sent: Export[Id, ValueTree[Token, Value]] = MapWithDefault.empty(ValueTree.empty)

  override def send(e: Export[Id, ValueTree[Token, Value]]): Unit = sent = e

  override def receive(): Import[Id, ValueTree[Token, Value]] = received
