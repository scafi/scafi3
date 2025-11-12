package it.unibo.scafi.libraries

import scala.scalanative.unsafe.{ exported, CString, Ptr }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.libraries.NativeFieldBasedSharedData.given
import it.unibo.scafi.message.HashInstances.cBinaryCodableHash
import it.unibo.scafi.nativebindings.aliases.NValues
import it.unibo.scafi.nativebindings.structs.{ BinaryCodable as CBinaryCodable, Field as CField }
import it.unibo.scafi.types.{ CMap, EqWrapper, NativeTypes, PortableTypes }
import it.unibo.scafi.utils.CUtils.{ asVoidPtr, freshPointer, fromSafeCString, toUnconfinedCString }
import it.unibo.scafi.utils.libraries.Iso
import it.unibo.scafi.utils.libraries.Iso.given

/**
 * A custom portable definition of a field-based `SharedData` structure for native platform.
 */
@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
trait NativeFieldBasedSharedData extends PortableLibrary:
  self: PortableTypes & NativeTypes =>

  override type Language <: AggregateFoundation & FieldBasedSharedData & {
    type DeviceId = EqWrapper[Ptr[CBinaryCodable]]
  }

  override type SharedData[Value] = Ptr[CField]

  override given [Value] => Iso[SharedData[Value], language.SharedData[Value]] = Iso(
    cFieldPtr =>
      val field = language.sharedDataApplicative.pure((!cFieldPtr).default_value.asInstanceOf[Value])
      val scalaNValues = CMap.of((!cFieldPtr).neighbor_values).toMap
      scalaNValues.foldLeft(field)((f, n) => f.set(EqWrapper(n._1), n._2))
    ,
    scalaField =>
      NativeFieldBasedSharedData.of(
        scalaField.default.asInstanceOf[Ptr[CBinaryCodable]],
        scalaField.neighborValues.map((id, v) => (deviceIdConversion(id), v)).toMap,
      ),
  )

  def withoutSelf[Value](field: Ptr[CField]): Seq[Ptr[CBinaryCodable]] =
    val scalaField = given_Iso_SharedData_SharedData.to(field)
    scalaField.withoutSelf.toSeq
end NativeFieldBasedSharedData

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object NativeFieldBasedSharedData:

  def of(default: Ptr[CBinaryCodable], neighborValues: Ptr[Byte]): Ptr[CField] =
    freshPointer[CField].tap: cFieldPtr =>
      (!cFieldPtr).default_value = default
      (!cFieldPtr).neighbor_values = neighborValues

  @exported("field_to_str")
  def fieldToString(sd: Ptr[CField]): CString =
    val defaultValuePtr = (!sd).default_value
    val defaultStr = fromSafeCString((!defaultValuePtr).to_str(defaultValuePtr))
    val neighborsStr = CMap
      .of[Ptr[CBinaryCodable], Ptr[CBinaryCodable]]((!sd).neighbor_values)
      .toMap
      .map((nid, nv) => fromSafeCString((!nid).to_str(nid)) + " -> " + fromSafeCString((!nv).to_str(nv)))
      .toList
      .sorted
      .mkString("[", ", ", "]")
    s"Field($defaultStr, $neighborsStr)".toUnconfinedCString // memory needs to be freed by the caller

  /** Neighbor values are not exposed to C client as CMap, rather as NValues alias, a.k.a. an opaque pointer. */
  given Iso[Ptr[Byte], NValues] = Iso(_.asInstanceOf[NValues], _.asInstanceOf[Ptr[Byte]])
end NativeFieldBasedSharedData
