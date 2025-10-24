package it.unibo.scafi.utils

import it.unibo.scafi.language.AggregateFoundation

object StringSharedDataOps:
  extension (using lang: AggregateFoundation)(data: lang.SharedData[String])
    infix def +(that: lang.SharedData[String]): lang.SharedData[String] = data.alignedMap(that)(_ + _)
    def toUpperCase: lang.SharedData[String] = data.mapValues(_.toUpperCase)
    def toLowerCase: lang.SharedData[String] = data.mapValues(_.toLowerCase)
    def length: lang.SharedData[Int] = data.mapValues(_.length)
    def trim: lang.SharedData[String] = data.mapValues(_.trim)
    def strip: lang.SharedData[String] = data.mapValues(_.strip)
    def stripLeading: lang.SharedData[String] = data.mapValues(_.stripLeading)
    def stripTrailing: lang.SharedData[String] = data.mapValues(_.stripTrailing)
    def isBlank: lang.SharedData[Boolean] = data.mapValues(_.isBlank)
    def isEmpty: lang.SharedData[Boolean] = data.mapValues(_.isEmpty)
    def charAt(index: Int): lang.SharedData[Char] = data.mapValues(_.charAt(index))
    def substring(beginIndex: Int, endIndex: Int): lang.SharedData[String] =
      data.mapValues(_.substring(beginIndex, endIndex))
    def contains(seq: String): lang.SharedData[Boolean] = data.mapValues(_.contains(seq))
    def startsWith(prefix: String): lang.SharedData[Boolean] = data.mapValues(_.startsWith(prefix))
    def endsWith(suffix: String): lang.SharedData[Boolean] = data.mapValues(_.endsWith(suffix))
    def replace(oldChar: Char, newChar: Char): lang.SharedData[String] = data.mapValues(_.replace(oldChar, newChar))
    def replaceAll(regex: String, replacement: String): lang.SharedData[String] =
      data.mapValues(_.replaceAll(regex, replacement))
    def split(regex: String): lang.SharedData[Array[String]] = data.mapValues(_.split(regex))
    def split(regex: String, limit: Int): lang.SharedData[Array[String]] = data.mapValues(_.split(regex, limit))
    def toCharArray: lang.SharedData[Array[Char]] = data.mapValues(_.toCharArray)
    def reverse: lang.SharedData[String] = data.mapValues(_.reverse)
    def mkString(sep: String): lang.SharedData[String] = data.mapValues(_.mkString(sep))
  end extension
end StringSharedDataOps
