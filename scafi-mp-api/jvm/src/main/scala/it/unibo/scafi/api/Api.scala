package it.unibo.scafi.api

object Api extends PortableXCApi:
  self =>

  object Interface extends self.Interface with ADTs with JVMTypes:

    override type PortableSharedData[Value] = Language#SharedData[Value]
    override given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] =
      Iso[PortableSharedData[T], language.SharedData[T]](data =>
        val localValue: language.SharedData[T] = data.default
        val neighbors = data.neighborValues
        neighbors.foldLeft(localValue)((f, n) => f.set(n._1, n._2)),
      )(identity)
