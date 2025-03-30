package it.unibo.scafi.collections

import it.unibo.scafi.UnitTest

class ValueTreeDefaultTests extends UnitTest with ValueTreeFactoryTests:
  "ValueTree default factory" should behave like valueTreeFactory(ValueTree)
