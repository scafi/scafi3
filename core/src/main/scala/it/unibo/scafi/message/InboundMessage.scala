package it.unibo.scafi.message

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.utils.Stack
import language.experimental.saferExceptions

trait InboundMessage:
  self: Stack & AggregateContext[DeviceId] =>

  type DeviceId

  private lazy val cachedPaths = new CachedPaths(importFromInboundMessages)

  protected def alignedDevices: Iterable[DeviceId] =
    if currentPath.isEmpty then cachedPaths.neighbors else cachedPaths.alignedDevicesAt(currentPath)

  protected def alignedMessages[Value]: Map[DeviceId, Value] = cachedPaths.dataAt(currentPath)

  override def neighbors: Iterable[DeviceId] = cachedPaths.neighbors

  private class CachedPaths(private val input: Import[DeviceId]):
    private lazy val cachedPaths: Map[Path[?], Map[DeviceId, Any]] =
      input.foldLeft(Map.empty):
        case (accumulator, (deviceId, valueTree)) =>
          valueTree.paths.foldLeft(accumulator): (accInner, path) =>
            val valueAtPath =
              try valueTree(path)
              catch
                case _: valueTree.NoPathFoundException =>
                  throw RuntimeException(s"Path: $path not found, this should not happen. Please report this.")
            accInner.updatedWith(path):
              case Some(existing) => Some(existing + (deviceId -> valueAtPath))
              case None => Some(Map(deviceId -> valueAtPath))

    lazy val neighbors: Iterable[DeviceId] = input.keySet + localId

    def alignedDevicesAt(tokens: IndexedSeq[InvocationCoordinate]): Iterable[DeviceId] =
      cachedPaths
        .filter: (p, _) =>
          p.startsWith(tokens)
        .values
        .flatMap(_.keySet)
        .toSet + localId

    def dataAt[Value](tokens: IndexedSeq[InvocationCoordinate]): Map[DeviceId, Value] =
      cachedPaths.get(Path(tokens *)).map(_.view.mapValues(_.asInstanceOf[Value]).toMap).getOrElse(Map.empty)
  end CachedPaths
end InboundMessage
