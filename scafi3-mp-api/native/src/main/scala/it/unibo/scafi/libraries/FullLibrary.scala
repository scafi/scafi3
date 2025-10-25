package it.unibo.scafi.libraries

import java.util.concurrent.atomic.AtomicReference

import scala.scalanative.unsafe.{ Ptr, Zone }

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.libraries.FullLibrary.libraryRef
import it.unibo.scafi.message.NativeBinaryCodable.nativeBinaryCodable
import it.unibo.scafi.nativebindings.structs.{
  AggregateLibrary as CAggregateLibrary,
  BinaryCodable as CBinaryCodable,
  Field as CField,
  FieldBasedSharedData as CFieldBasedSharedData,
  ReturnSending as CReturnSending,
}
import it.unibo.scafi.types.{ CMap, EqWrapper, NativeTypes }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
class FullLibrary(using
    lang: AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData & FieldCalculusSyntax,
) extends FullPortableLibrary
    with NativeFieldBasedSharedData
    with NativeTypes:

  override given valueCodable[Value, Format]: UniversalCodable[Value, Format] =
    nativeBinaryCodable.asInstanceOf[UniversalCodable[Value, Format]]

  override given deviceIdConv[ID]: Conversion[language.DeviceId, ID] =
    _.asInstanceOf[EqWrapper[Ptr[CBinaryCodable]]].value.asInstanceOf[ID]

  override type ReturnSending = Ptr[CReturnSending]

  override given [Value] => Conversion[ReturnSending, RetSend[language.SharedData[Value]]] = rs =>
    RetSend((!rs).returning, (!rs).sending)

  def asNative(using Zone): Ptr[CAggregateLibrary] =
    libraryRef.set(this)
    val lib = CAggregateLibrary()
    (!lib).Field = !CFieldBasedSharedData(default => NativeFieldBasedSharedData.of(default, CMap.empty))
    (!lib).local_id = () => libraryRef.get().localId.asInstanceOf[Ptr[CBinaryCodable]]
    (!lib).branch = (condition: Boolean, trueBranch: Function0[Ptr[Byte]], falseBranch: Function0[Ptr[Byte]]) =>
      libraryRef.get().branch_(condition)(trueBranch)(falseBranch)
    (!lib).exchange = (initial: Ptr[CField], f: Function1[Ptr[CField], ReturnSending]) =>
      libraryRef.get().exchange_(initial)(f)
    (!lib).share = (initial: Ptr[CBinaryCodable], f: Function1[Ptr[CField], Ptr[CBinaryCodable]]) =>
      libraryRef.get().share_(initial)(f)
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
