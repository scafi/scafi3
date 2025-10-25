package it.unibo.scafi.libraries

import scala.scalanative.posix.string.strdup
import scala.scalanative.unsafe.{ exported, fromCString, toCString, CString, Ptr, Zone }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.libraries.NativeFieldBasedSharedData.given
import it.unibo.scafi.message.CBinaryCodable.given_Hash_Ptr
import it.unibo.scafi.message.NativeBinaryCodable.nativeBinaryCodable
import it.unibo.scafi.nativebindings.aliases.NValues
import it.unibo.scafi.nativebindings.structs.{ BinaryCodable as CBinaryCodable, Field as CField }
import it.unibo.scafi.types.{ CMap, EqWrapper, NativeTypes, PortableTypes }
import it.unibo.scafi.utils.CUtils.{ asVoidPtr, freshPointer }
import it.unibo.scafi.utils.libraries.Iso
import it.unibo.scafi.utils.libraries.Iso.given

/**
 * A custom portable definition of a field-based `SharedData` structure for native platform.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait NativeFieldBasedSharedData extends PortableLibrary:
  self: PortableTypes & PortableExchangeCalculusLibrary & NativeTypes =>

  override type Language <: AggregateFoundation & ExchangeSyntax & FieldBasedSharedData

  override type SharedData[Value] = Ptr[CField]

  override given [Value] => Iso[SharedData[Value], language.SharedData[Value]] = Iso(
    cFieldPtr =>
      val field = language.sharedDataApplicative.pure((!cFieldPtr).default_value.asInstanceOf[Value])
      val scalaNValues = CMap.of((!cFieldPtr).neighbor_values).toMap
      scalaNValues.foldLeft(field): (f, n) =>
        f.set(EqWrapper(n._1).asInstanceOf[language.DeviceId], n._2)
    ,
    scalaField =>
      NativeFieldBasedSharedData.of(
        scalaField.default.asInstanceOf[Ptr[CBinaryCodable]],
        scalaField.neighborValues.map((id, v) => (deviceIdConv(id), v)).toMap,
      ),
  )
end NativeFieldBasedSharedData

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeFieldBasedSharedData:

  def of(default: Ptr[CBinaryCodable], neighborValues: Ptr[Byte]): Ptr[CField] =
    nativeBinaryCodable.register(default)
    freshPointer[CField].tap: cFieldPtr =>
      (!cFieldPtr).default_value = default
      (!cFieldPtr).neighbor_values = neighborValues

  @exported("field_to_str")
  def fieldToString(sd: Ptr[CField]): CString =
    val defaultValuePtr = (!sd).default_value
    val defaultStr = fromCString((!defaultValuePtr).to_str(defaultValuePtr))
    val neighborsStr = CMap
      .of[Ptr[CBinaryCodable], Ptr[CBinaryCodable]]((!sd).neighbor_values)
      .toMap
      .map((nid, nv) => fromCString((!nid).to_str(nid)) + " -> " + fromCString((!nv).to_str(nv)))
      .toList
      .sorted
      .mkString("[", ", ", "]")
    Zone(strdup(toCString(s"Field($defaultStr, $neighborsStr)"))) // memory needs to be freed by the caller

  /** Neighbor values are not exposed to C client as CMap, rather as NValues alias, a.k.a. an opaque pointer. */
  given Iso[Ptr[Byte], NValues] = Iso(_.asInstanceOf[NValues], _.asInstanceOf[Ptr[Byte]])
end NativeFieldBasedSharedData
