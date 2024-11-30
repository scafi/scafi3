package it.unibo.field4s.collections

import it.unibo.field4s.UnitTest

class ValueTreeDefaultTests extends UnitTest with ValueTreeFactoryTests:
  "ValueTree default factory" should behave like valueTreeFactory(ValueTree)
