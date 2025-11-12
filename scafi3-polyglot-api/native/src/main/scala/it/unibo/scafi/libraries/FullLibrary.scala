package it.unibo.scafi.libraries

import java.util.concurrent.atomic.AtomicReference

import scala.scalanative.unsafe.{ exported, Ptr, Zone }

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.libraries.FullLibrary.libraryRef
import it.unibo.scafi.message.Codable
import it.unibo.scafi.message.NativeCodable.nativeCodable
import it.unibo.scafi.nativebindings.all.{
  AggregateLibrary as CAggregateLibrary,
  Array as CArray,
  BinaryCodable as CBinaryCodable,
  DeviceId as CDeviceId,
  Field as CField,
  FieldBasedSharedData as CFieldBasedSharedData,
}
import it.unibo.scafi.types.{ CMap, EqWrapper, NativeTypes }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
class FullLibrary(using
    lang: AggregateFoundation & BranchingSyntax & FieldBasedSharedData & FieldCalculusSyntax & {
      type DeviceId = EqWrapper[Ptr[CBinaryCodable]]
    },
) extends FullPortableLibrary
    with NativeFieldBasedSharedData
    with NativeTypes:

  override given valueCodable[Value, Format]: Conversion[Value, Codable[Value, Format]] =
    nativeCodable.asInstanceOf[Conversion[Value, Codable[Value, Format]]]

  override given deviceIdConversion[ID]: Conversion[language.DeviceId, ID] = _.value.asInstanceOf[ID]

  def asNative(using Zone): Ptr[CAggregateLibrary] =
    libraryRef.set(this)
    val lib = CAggregateLibrary()
    (!lib).Field = !CFieldBasedSharedData(default => NativeFieldBasedSharedData.of(default, CMap.empty))
    (!lib).local_id = () => CDeviceId(libraryRef.get().localId)
    (!lib).branch = (condition: Boolean, trueBranch: Function0[Ptr[Byte]], falseBranch: Function0[Ptr[Byte]]) =>
      libraryRef.get().branch(condition)(trueBranch)(falseBranch)
    (!lib).evolve = (initial: Ptr[Byte], evolution: Function1[Ptr[Byte], Ptr[Byte]]) =>
      libraryRef.get().evolve(initial)(evolution)
    (!lib).share = (initial: Ptr[CBinaryCodable], f: Function1[Ptr[CField], Ptr[CBinaryCodable]]) =>
      libraryRef.get().share(initial)(f)
    (!lib).neighbor_values = (value: Ptr[CBinaryCodable]) => libraryRef.get().neighborValues(value)
    lib
end FullLibrary

object FullLibrary:

  /**
   * Singleton reference to the full library instance. Unfortunately, due to limitations in C, it is not possible to
   * close a function pointer over local state parameters, so we need to store the instance in a global variable.
   *
   * This is not ideal, but does not cause issues in practice since rounds are executed sequentially and independently.
   */
  private[FullLibrary] val libraryRef = new AtomicReference[FullLibrary]()

  @exported("without_self")
  def withoutSelf(field: Ptr[CField]): Ptr[CArray] = libraryRef.get().withoutSelf(field)
