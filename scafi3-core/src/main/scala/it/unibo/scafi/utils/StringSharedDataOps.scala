package it.unibo.scafi.utils

import it.unibo.scafi.language.xc.FieldBasedSharedData

import cats.syntax.all.*

object StringSharedDataOps:
  extension (using lang: FieldBasedSharedData)(fieldData: lang.SharedData[String])
    infix def +(that: lang.SharedData[String]): lang.SharedData[String] = (fieldData, that).mapN(_ + _)
    def toUpperCase: lang.SharedData[String] = fieldData.map(_.toUpperCase)
    def toLowerCase: lang.SharedData[String] = fieldData.map(_.toLowerCase)
    def length: lang.SharedData[Int] = fieldData.map(_.length)
    def trim: lang.SharedData[String] = fieldData.map(_.trim)
    def strip: lang.SharedData[String] = fieldData.map(_.strip)
    def stripLeading: lang.SharedData[String] = fieldData.map(_.stripLeading)
    def stripTrailing: lang.SharedData[String] = fieldData.map(_.stripTrailing)
    def isBlank: lang.SharedData[Boolean] = fieldData.map(_.isBlank)
    def isEmpty: lang.SharedData[Boolean] = fieldData.map(_.isEmpty)
    def charAt(index: Int): lang.SharedData[Char] = fieldData.map(_.charAt(index))
    def substring(beginIndex: Int, endIndex: Int): lang.SharedData[String] =
      fieldData.map(_.substring(beginIndex, endIndex))
    def contains(seq: String): lang.SharedData[Boolean] = fieldData.map(_.contains(seq))
    def startsWith(prefix: String): lang.SharedData[Boolean] = fieldData.map(_.startsWith(prefix))
    def endsWith(suffix: String): lang.SharedData[Boolean] = fieldData.map(_.endsWith(suffix))
    def replace(oldChar: Char, newChar: Char): lang.SharedData[String] =
      fieldData.map(_.replace(oldChar, newChar))
    def replaceAll(regex: String, replacement: String): lang.SharedData[String] =
      fieldData.map(_.replaceAll(regex, replacement))
    def split(regex: String): lang.SharedData[Array[String]] = fieldData.map(_.split(regex))
    def split(regex: String, limit: Int): lang.SharedData[Array[String]] = fieldData.map(_.split(regex, limit))
    def toCharArray: lang.SharedData[Array[Char]] = fieldData.map(_.toCharArray)
    def reverse: lang.SharedData[String] = fieldData.map(_.reverse)
  end extension
end StringSharedDataOps
