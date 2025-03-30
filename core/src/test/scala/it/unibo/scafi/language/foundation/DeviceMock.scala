package it.unibo.scafi.language.foundation

import it.unibo.scafi.engine.path.Path
import it.unibo.scafi.language.AggregateFoundation

trait DeviceMock:
  this: AggregateFoundation & FieldMock =>

  override type DeviceId = Int

  override def self: DeviceId = 0

  override def device: SharedData[DeviceId] = mockField(Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))

  override def align[T](path: Any)(body: () => T): T = body()
