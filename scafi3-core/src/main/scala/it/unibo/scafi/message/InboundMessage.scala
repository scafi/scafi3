package it.unibo.scafi.message

import it.unibo.scafi.context.AggregateContext
import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.message.Decodable.decode
import it.unibo.scafi.message.ValueTree.NoPathFoundException
import it.unibo.scafi.utils.{ AlignmentManager, InvocationCoordinate }

trait InboundMessage:
  self: AlignmentManager & AggregateContext & AggregateFoundation =>

  private lazy val cachedPaths = new CachedPaths(importFromInboundMessages)

  protected def alignedDevices: Iterable[DeviceId] =
    if currentPath.isEmpty then cachedPaths.neighbors else cachedPaths.alignedDevicesAt(currentPath)

  protected def alignedMessages[Format, Value: DecodableFrom[Format]]: Map[DeviceId, Value] =
    cachedPaths.dataAt(currentPath)

  override def neighbors: Set[DeviceId] = cachedPaths.neighbors

  private class CachedPaths(private val input: Import[DeviceId]):
    private lazy val cachedPaths: Map[Path, Map[DeviceId, Any]] =
      input.foldLeft(Map.empty):
        case (accumulator, (deviceId, valueTree)) =>
          valueTree.paths.foldLeft(accumulator): (accInner, path) =>
            val valueAtPath =
              try valueTree.apply[Any](path) // Here scala type inference fails badly...
              catch
                case _: NoPathFoundException =>
                  throw RuntimeException(s"Path: $path not found, this should not happen. Please report this.")
            accInner.updatedWith(path):
              case Some(existing) => Some(existing + (deviceId -> valueAtPath))
              case None => Some(Map(deviceId -> valueAtPath))

    lazy val neighbors: Set[DeviceId] = input.neighbors + localId

    def alignedDevicesAt(path: Path): Iterable[DeviceId] =
      cachedPaths
        .filter: (cachedPath, _) =>
          cachedPath.startsWith(path)
        .values
        .flatMap(_.keySet)
        .toSet + localId

    @SuppressWarnings(Array("DisableSyntax.asInstanceOf"))
    def dataAt[Format, Value: DecodableFrom[Format]](path: Path): Map[DeviceId, Value] =
      val selfValueAtPath = selfMessagesFromPreviousRound
        .get[Format](path)
        .map(localId -> decode(_))
      val importedValuesAtPath = cachedPaths
        .get(path)
        .map(_.view.mapValues(_.asInstanceOf[Format]).mapValues(decode).toMap)
        .getOrElse(Map.empty)
      selfValueAtPath.fold(importedValuesAtPath)(importedValuesAtPath + _)
  end CachedPaths
end InboundMessage
