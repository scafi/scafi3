package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

object StringSharedDataOps:
  extension (using lang: FieldBasedSharedData)(fieldData: lang.SharedData[String])
    infix def +(that: lang.SharedData[String]): lang.SharedData[String] = fieldData.alignedMap(that)(_ + _)
    def toUpperCase: lang.SharedData[String] = fieldData.mapValues(_.toUpperCase)
    def toLowerCase: lang.SharedData[String] = fieldData.mapValues(_.toLowerCase)
    def length: lang.SharedData[Int] = fieldData.mapValues(_.length)
    def trim: lang.SharedData[String] = fieldData.mapValues(_.trim)
    def strip: lang.SharedData[String] = fieldData.mapValues(_.strip)
    def stripLeading: lang.SharedData[String] = fieldData.mapValues(_.stripLeading)
    def stripTrailing: lang.SharedData[String] = fieldData.mapValues(_.stripTrailing)
    def isBlank: lang.SharedData[Boolean] = fieldData.mapValues(_.isBlank)
    def isEmpty: lang.SharedData[Boolean] = fieldData.mapValues(_.isEmpty)
    def charAt(index: Int): lang.SharedData[Char] = fieldData.mapValues(_.charAt(index))
    def substring(beginIndex: Int, endIndex: Int): lang.SharedData[String] =
      fieldData.mapValues(_.substring(beginIndex, endIndex))
    def contains(seq: String): lang.SharedData[Boolean] = fieldData.mapValues(_.contains(seq))
    def startsWith(prefix: String): lang.SharedData[Boolean] = fieldData.mapValues(_.startsWith(prefix))
    def endsWith(suffix: String): lang.SharedData[Boolean] = fieldData.mapValues(_.endsWith(suffix))
    def replace(oldChar: Char, newChar: Char): lang.SharedData[String] =
      fieldData.mapValues(_.replace(oldChar, newChar))
    def replaceAll(regex: String, replacement: String): lang.SharedData[String] =
      fieldData.mapValues(_.replaceAll(regex, replacement))
    def split(regex: String): lang.SharedData[Array[String]] = fieldData.mapValues(_.split(regex))
    def split(regex: String, limit: Int): lang.SharedData[Array[String]] = fieldData.mapValues(_.split(regex, limit))
    def toCharArray: lang.SharedData[Array[Char]] = fieldData.mapValues(_.toCharArray)
    def reverse: lang.SharedData[String] = fieldData.mapValues(_.reverse)
  end extension
end StringSharedDataOps
