package it.unibo.scafi.api

object Api extends PortableXCApi:
  self =>

  object Interface extends self.Interface with ADTs with JVMTypes:

    override type PortableSharedData[Value] = Language#SharedData[Value]
    override given [T](using language: Language): Iso[PortableSharedData[T], language.SharedData[T]] =
      Iso[PortableSharedData[T], language.SharedData[T]](_.asInstanceOf[language.SharedData[T]])(
        _.asInstanceOf[PortableSharedData[T]],
      )
